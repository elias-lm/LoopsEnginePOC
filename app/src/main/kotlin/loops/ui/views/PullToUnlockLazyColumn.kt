package loops.ui.views

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun PullToUnlockLazyColumn(
    modifier: Modifier,
    pullToUnlockState: PTUState,
    indicator: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {
    PullToUnlockLazyColumn(
        modifier = modifier,
        lockPosition = pullToUnlockState.lockPosition,
        unlockThreshold = pullToUnlockState.triggerThreshold,
        pullToUnlockState = pullToUnlockState,
        indicator = indicator,
        content = content
    )
}

@Composable
fun PullToUnlockLazyColumn(
    modifier: Modifier,
    lockPosition: List<Int>,
    unlockThreshold: Dp,
    pullToUnlockState: PTUState = rememberPullToUnlockState(
        lockPosition = lockPosition,
        threshold = unlockThreshold,
        lazyListState = rememberLazyListState(
            initialFirstVisibleItemIndex = lockPosition[0]
        )
    ),
    indicator: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {
    ActualPTUWithIndicator(
        modifier = modifier,
        lockPosition = pullToUnlockState.lockPosition,
        unlockThreshold = pullToUnlockState.triggerThreshold,
        pullToUnlockState = pullToUnlockState,
        indicator = indicator,
        content = content
    )
}

@Composable
fun ActualPTUWithIndicator(
    modifier: Modifier,
    lockPosition: List<Int>,
    unlockThreshold: Dp,
    pullToUnlockState: PTUState = rememberPullToUnlockState(
        lockPosition = lockPosition,
        threshold = unlockThreshold,
        lazyListState = rememberLazyListState(
            initialFirstVisibleItemIndex = lockPosition[0]
        )
    ),
    indicator: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToUnlock(
                state = pullToUnlockState,
                currentFirstVisibleItem = {
                    pullToUnlockState.lazyListState.firstVisibleItemIndex
                },
                stopScroll = {
                    pullToUnlockState.lazyListState.stopScroll(MutatePriority.UserInput)
                    pullToUnlockState.lazyListState.animateScrollToItem(it)
                },
            )
            .then(modifier)
    ) {
        indicator()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = pullToUnlockState.lazyListState
        ) {
            content()
        }
    }
}