package loops.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import loops.ui.screens.ScreenDestinations

class Navigator {

    private val destinationFlow = MutableSharedFlow<ScreenDestinations>(extraBufferCapacity = 1)
    var destination: Flow<ScreenDestinations> = destinationFlow.asSharedFlow()

    fun navigateTo(destination: ScreenDestinations) {
        destinationFlow.tryEmit(destination)
    }
}