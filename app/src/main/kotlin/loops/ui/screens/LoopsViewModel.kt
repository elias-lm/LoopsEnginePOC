package loops.ui.screens

import android.content.res.AssetManager
import android.media.AudioManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsoft.snippet.Snippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    val masterFrame = periodicUpdate({
        loopsEngine.getMasterFrame()
    }, 100)

    val currentMaster = periodicUpdate({
        loopsEngine.getCurrentMasterIndex()
    }, 100)

    fun triggerDown(id: Int) {
        loopsEngine.trigger(id)
        Snippet.capture {

        }
    }

    fun triggerUp(id: Int) {
        loopsEngine.stopTrigger(id)
    }

    fun fileNameForIndex(index: Int): String {
        return loopsEngine.getFileNameForIndex(index).split("/")[1].removePrefix("SpacePiretsBand ")
            .removeSuffix(".wav")
    }

    fun getCurrentFrame(index: Int): Int {
        return loopsEngine.getCurrentFrameForIndex(index)
    }

    fun getMaxFrames(index: Int): Int {
        return loopsEngine.getMaxFramesForIndex(index)
    }

    fun getCurrentMaster(): Int {
        return loopsEngine.getCurrentMasterIndex()
    }

    fun getMasterFrame(): Int {
        return loopsEngine.getMasterFrame()
    }

    fun ViewModel.periodicUpdate(
        updateFunction: () -> Int,
        period: Long
    ): MutableState<Int> {
        val mutableState = mutableStateOf(0)
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            while (true) {
                mutableState.value = updateFunction()
                delay(period)
            }
        }
        return mutableState
    }
}