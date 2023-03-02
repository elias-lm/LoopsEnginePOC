package loops.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun rememberCustomLazyListState(overTopCount: Int) =
    rememberSaveable(stateSaver = CustomLazyListState.Saver) {
        mutableStateOf(CustomLazyListState(overTopCount))
    }

class CustomLazyListState(var overTopCount: Int) {
    companion object {
        val Saver: Saver<CustomLazyListState, *> = listSaver(
            save = { listOf(it.overTopCount) },
            restore = {
                CustomLazyListState(
                    overTopCount = it[0],
                )
            }
        )
    }

}

@Composable
fun CustomLazyList( //UnlockSwipe
    state: CustomLazyListState,// = rememberCustomLazyListState(0)
    content: LazyListScope.() -> Unit
) {
    var debugText by remember {
        mutableStateOf("")
    }
    val coroutine = rememberCoroutineScope()
    val topVisibleItem = state.overTopCount
    val lazyListState = rememberLazyListState(topVisibleItem)
    var futureScrollAllowed by remember { mutableStateOf(false) }
    val firstItemIndex by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex
        }
    }

    if (firstItemIndex <= state.overTopCount && !futureScrollAllowed) {
        coroutine.launch {
            lazyListState.stopScroll(MutatePriority.UserInput)
            lazyListState.animateScrollToItem(state.overTopCount)
        }
    }


    var scrollState by remember {
        mutableStateOf(false)
    }
    val mutatorMutex = remember {
        MutatorMutex()
    }
    val indicatorOffset = remember {
        Animatable(0f)
    }

    fun animateOffset(delta: Float) = coroutine.launch {
        mutatorMutex.mutate {
            indicatorOffset.animateTo(delta)
        }
    }

    fun setOffset(delta: Float) = coroutine.launch {
        mutatorMutex.mutate {
            indicatorOffset.snapTo(indicatorOffset.value + delta)
        }
    }

    LaunchedEffect(scrollState) {
        if (!scrollState) {
            // If there's not a swipe in progress, rest the indicator at 0f
            indicatorOffset.animateTo(0f)
        }
    }

    fun consumeVerticalScroll(available: Float): Float {
        if (indicatorOffset.value > 300 && !futureScrollAllowed) {
            animateOffset(0f)
            futureScrollAllowed = true
            return 0f
        }
        val DragMultiplier = 0.5f
        if (available > 0) {
            scrollState = true
        } else if (indicatorOffset.value.roundToInt() == 0) {
            scrollState = false
        }

        val newOffset = (available + indicatorOffset.value).coerceAtLeast(0f)
        val dragConsumed = newOffset - indicatorOffset.value

        return if (dragConsumed.absoluteValue >= 0.5f) {
            setOffset(dragConsumed)
            // Return the consumed Y
//            dragConsumed / DragMultiplier
            available
        } else {
            0f
        }
    }

    val verticalScrollConsumer = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val scrollingToTop = available.y > 0
                val scrollThreshold = available.y.toInt() in 30..100
                return if (scrollingToTop) {
                    when (futureScrollAllowed) {
                        true -> {
                            Offset.Zero
                        }
                        false -> {
                            if(source == NestedScrollSource.Fling)
                                available
                            //function that consumes "available" and returns whats left
                            if (firstItemIndex <= state.overTopCount && source == NestedScrollSource.Drag) {
                                Offset(
                                    0f,
                                    consumeVerticalScroll(available.y)
                                )
                            } else {
                                Offset.Zero
                            }
                        }
                    }
                } else {
                    futureScrollAllowed = firstItemIndex <= state.overTopCount
                    Offset.Zero
                }
            }

        }
    }

    Column {
        Text(text = "debugText::: $debugText")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(verticalScrollConsumer),
        ) {
            RedBox { indicatorOffset.value.toInt() }
//            SwipeRefresh(
//                state = rememberSwipeRefreshState(isRefreshing = false),
//                onRefresh = { debugText = "refresh!" },
//                indicator = { s, r ->
//                    debugText = s.toString() + " ###### " + r.value
//                }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = lazyListState
            ) {
                content()
            }
//            }
        }
    }
}


@Composable
fun RedBox(modifier: Modifier = Modifier, offset: () -> Int) {
    Row(
        modifier = Modifier
            .offset {
                IntOffset(0, offset())
            }
            .fillMaxWidth()
            .wrapContentSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(Color.Red)
                .then(modifier)
        )
    }
}

@Composable
fun MyFling(
    flingDecay: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
): FlingBehavior {
    return object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            return if (abs(initialVelocity) > 1f) {
                var velocityLeft = initialVelocity
                var lastValue = 0f
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity,
                ).animateDecay(flingDecay) {
                    val delta = value - lastValue
                    val consumed = scrollBy(delta)
                    lastValue = value
                    velocityLeft = this.velocity
                    // avoid rounding errors and stop if anything is unconsumed
                    if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
                }
                velocityLeft
            } else {
                initialVelocity
            }
        }
    }
}