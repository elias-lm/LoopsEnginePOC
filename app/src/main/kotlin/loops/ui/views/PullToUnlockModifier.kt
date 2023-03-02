package loops.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

//TODO
// auto lock when scroll away toggle
// lock from all sides
fun Modifier.pullToUnlock(
    state: PTUState,
    currentFirstVisibleItem: () -> Int,
    stopScroll: suspend (firstItem: Int) -> Unit,
) = composed {

    val coroutineScope = rememberCoroutineScope()
    val triggerThreshold = with(LocalDensity.current) { state.triggerThreshold.toPx() }

    nestedScroll(
        PTUNestedScrollConnection(
            indicatorOffset = state.indicatorOffset,
        ) { isDrag: Boolean, newIndicatorOffset: Float, scrollDirection: PTUNestedScrollConnection.ScrollDirection ->
            with(state) {
                isSwipeInProgress =
                    scrollDirection == PTUNestedScrollConnection.ScrollDirection.ToTop

                val currentTopIndex = currentFirstVisibleItem()

                singleUnlockStep(
                    currentTopItem = currentTopIndex,
                    lockPosition = lockPosition,
                    currentLockIndex = lockIndex,
                    isDrag = isDrag,
                    currentIndicatorOffset = newIndicatorOffset,
                    triggerThreshold = triggerThreshold,
                    setUnlocked = {
                        if (it) lockIndex++
                        else lockIndex--
                    },
                    coroutineScope = coroutineScope,
                    dispatchScrollDelta = { state.dispatchScrollDelta(newIndicatorOffset) },
                    resetIndicatorOffset = { state.animateOffsetTo(0f) },
                    stopScroll = { stopScroll(it) }
                )

            }
        }
    )
        .then(this)
}


private fun singleUnlockStep(
    currentTopItem: Int,
    lockPosition: List<Int>,
    currentLockIndex: Int,
    isDrag: Boolean,
    currentIndicatorOffset: Float,
    triggerThreshold: Float,
    setUnlocked: (Boolean) -> Unit,
    coroutineScope: CoroutineScope,
    dispatchScrollDelta: suspend () -> Unit,
    resetIndicatorOffset: suspend () -> Unit,
    stopScroll: suspend (Int) -> Unit,
): Boolean {

    val firstIndex = when {
        currentLockIndex <= 0 -> 0
        currentLockIndex >= lockPosition.size -> lockPosition.size - 1
        else -> currentLockIndex - 1
    }


    val lastIndex = when {
        currentLockIndex <= 0 -> 0
        currentLockIndex >= lockPosition.size -> lockPosition.size
        else -> currentLockIndex
    }

    val top = if (lastIndex >= lockPosition.size) 0 else lockPosition[lastIndex]
    val bottom = /*=TODO should be max list size if(firstIndex == 0) lockPosition.size else*/
        lockPosition[firstIndex]

//    println("currentLockIndex: $currentLockIndex currentTop: $currentTopItem firstIndex: $firstIndex lastIndex: $lastIndex top: $top bottom: $bottom")

    return if (currentTopItem <= top) {
        //try unlock
        coroutineScope.launch {
            if (isDrag) {
                dispatchScrollDelta()
                if (currentIndicatorOffset > triggerThreshold && currentLockIndex <= lockPosition.size) {
                    setUnlocked(true)
                }
                resetIndicatorOffset()
            } else {
                stopScroll(top)
            }
        }
        true
    } else if (currentTopItem in (top + 1) until bottom) {
        //do nothing
        false
    } else if (currentTopItem >= bottom) {
        //lock
        if (currentLockIndex > 0)
            setUnlocked(false)
        false
    } else {
        //never reached here
        false
    }
}


//working
//singleUnlockStep(
//currentTopItem = currentTopIndex,
//lockPosition = lockPosition[0],
//isUnlocked = state.isUnlocked,
//isDrag = isDrag,
//currentIndicatorOffset = newIndicatorOffset,
//triggerThreshold = triggerThreshold,
//setUnlocked = { state.isUnlocked = it },
//coroutineScope = coroutineScope,
//dispatchScrollDelta = { state.dispatchScrollDelta(newIndicatorOffset) },
//resetIndicatorOffset = { state.animateOffsetTo(0f) },
//stopScroll = { stopScroll(it) }
//)

private fun singleUnlockStep(
    currentTopItem: Int,
    lockPosition: Int,
    isUnlocked: Boolean,
    isDrag: Boolean,
    currentIndicatorOffset: Float,
    triggerThreshold: Float,
    setUnlocked: (Boolean) -> Unit,
    coroutineScope: CoroutineScope,
    dispatchScrollDelta: suspend () -> Unit,
    resetIndicatorOffset: suspend () -> Unit,
    stopScroll: suspend (Int) -> Unit,
) =
    if (currentTopItem >= lockPosition) {
        setUnlocked(false)
        false
    } else {
        if (!isUnlocked) {
            coroutineScope.launch {
                if (isDrag) {
                    dispatchScrollDelta()
                    if (currentIndicatorOffset > triggerThreshold) {
                        setUnlocked(true)
                    }
                    resetIndicatorOffset()
                } else {
                    stopScroll(lockPosition)
                }
            }
            true
        } else {
            false
        }
    }


@Composable
fun rememberPullToUnlockState(
    lockPosition: List<Int>,
    threshold: Dp,
    lazyListState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = lockPosition[0]
    )
) = remember {
    PTUState(
        lockPosition = lockPosition,
        triggerThreshold = threshold,
        lazyListState = lazyListState
    )
}

class PTUState(
    val lockPosition: List<Int>,
    val triggerThreshold: Dp,
    val lazyListState: LazyListState,
) {

    private val _indicatorOffset = Animatable(0f)
    private val mutatorMutex = MutatorMutex()

    var isUnlocked: Boolean by mutableStateOf(false)
    var isSwipeInProgress: Boolean by mutableStateOf(false)
    var isEnabled: Boolean by mutableStateOf(false)

    //TESTING
    var lockIndex: Int by mutableStateOf(0)

    //todo change to dp?
    val indicatorOffset: Float get() = _indicatorOffset.value

    suspend fun animateOffsetTo(offset: Float) {
        mutatorMutex.mutate {
            _indicatorOffset.animateTo(offset)
        }
    }

    suspend fun dispatchScrollDelta(delta: Float) {
        mutatorMutex.mutate {
            _indicatorOffset.snapTo(delta)
        }
    }

//    companion object {
//        //TODO
//        val Saver = listSaver<PTUState, Any>(
//            save = {
//                listOf(it.lockPosition, it.threshold, it.lazyListState)
//            },
//            restore = {
//                PTUState(it[0] as Int, it[1] as Dp, it[2] as LazyListState)
//            }
//        )
//    }
}

//PTU = Pull to unlock
class PTUNestedScrollConnection(
    private val indicatorOffset: Float,
    private val onScroll: (isDrag: Boolean, delta: Float, scrollDirection: ScrollDirection) -> Boolean
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return when (scrollDirection(available)) {
            ScrollDirection.ToTop -> onScroll(
                source == NestedScrollSource.Drag,
                available,
                ScrollDirection.ToTop
            )
            else -> {
                onScroll(
                    source == NestedScrollSource.Drag,
                    available,
                    ScrollDirection.ToBottom
                )
                Offset.Zero
            }
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return when (scrollDirection(available)) {
            ScrollDirection.ToBottom -> {
                onScroll(
                    source == NestedScrollSource.Drag,
                    available,
                    ScrollDirection.ToBottom
                )
            }
            else -> {
                onScroll(
                    source == NestedScrollSource.Drag,
                    available,
                    ScrollDirection.ToTop
                )
                Offset.Zero
            }
        }
    }

    private fun onScroll(
        isDrag: Boolean,
        available: Offset,
        scrollDirection: ScrollDirection
    ): Offset {
        val availableOffset = available.y
        val newOffset = (availableOffset + indicatorOffset)
        val dragConsumed = newOffset - indicatorOffset

        return if (dragConsumed.absoluteValue >= 0.5f || dragConsumed.absoluteValue <= -0.5f) {
            val consumed = onScroll(isDrag, indicatorOffset + dragConsumed, scrollDirection)
            if (consumed) available else Offset.Zero
        } else
            Offset.Zero
    }

    enum class ScrollDirection {
        ToBottom, //Finger sliding up
        ToTop //Finger sliding down
    }

    private fun scrollDirection(available: Offset): ScrollDirection {
        return if (available.y > 0) ScrollDirection.ToTop else ScrollDirection.ToBottom
    }
}