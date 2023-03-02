package loops.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import loops.ui.Navigator

class MainViewModel(
    val savedStateHandle: SavedStateHandle,
    val navigator: Navigator,
) : ViewModel() {

}