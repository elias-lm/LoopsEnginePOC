package loops.ui.screens

import android.content.res.AssetManager
import android.media.AudioManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsoft.snippet.Snippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import loops.LoopsEngine
import loops.ui.Navigator


class LoopsViewModel(
    //injected by Koin
    private val savedStateHandle: SavedStateHandle,
    private val navigator: Navigator,
    private val assetsManager: AssetManager,
    private val audioManager: AudioManager,
    private val loopsEngine: LoopsEngine,
) : ViewModel() {

    fun triggerDown(id: Int) {
        loopsEngine.trigger(id)
        Snippet.capture {

        }
    }

    fun triggerUp(id: Int) {
        loopsEngine.stopTrigger(id)
    }

    fun fileOnIndex(index: Int): String {
        return loopsEngine.getFileNameForIndex(index).split("/")[1].removeSuffix(".wav")
    }

}