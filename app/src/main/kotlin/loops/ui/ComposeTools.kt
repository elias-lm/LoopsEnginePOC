package loops.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.StateFlow


@Composable
fun <T> CollectEffect(
    stateFlow: StateFlow<T?>,
    key: String = stateFlow.toString(),
    collect: (T) -> Unit
) = LaunchedEffect(key1 = key) {
    stateFlow.collect {
        if (it != null)
            collect(it)
    }
}
