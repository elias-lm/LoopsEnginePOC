//package loops.ui.screens
//
//import android.content.res.AssetFileDescriptor
//import android.content.res.AssetManager
//import android.media.AudioManager
//import android.media.AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER
//import android.media.AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE
//import android.media.MediaPlayer
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.github.qingmei2.soundtouch.SoundStreamAudioPlayer
//import com.github.qingmei2.soundtouch.SoundTouch
//import kotlinx.coroutines.CancellableContinuation
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.async
//import kotlinx.coroutines.cancelAndJoin
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//import loops.ui.Navigator
//import kotlin.random.Random
//
//
//class LoopsViewModel(
//    //injected by Koin
//    private val savedStateHandle: SavedStateHandle,
//    private val navigator: Navigator,
//    private val assetsManager: AssetManager,
//    private val audioManager: AudioManager
//) : ViewModel() {
//
//    val sounds = mutableListOf<MelodyController>()
//    var masterBPM by mutableStateOf(0f)
//    var masterMC: MelodyController? by mutableStateOf(null)
//
//    init {
//        loadSounds()
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        closeMediaPlayers()
//    }
//
//    fun closeMediaPlayers() {
//        sounds.forEach {
//            it.close()
//        }
//        masterBPM = 0f
//        masterMC = null
//        sounds.clear()
//        loadSounds()
//    }
//
//    fun loadSounds() {
//        assetsManager.list("SpaceGypsiesBand/")?.filter { it.contains(".wav") }?.forEach { a ->
//            viewModelScope.launch {
//                try {
//                    sounds.add(
//                        MelodyController(
//                            Melody(
//                                title = a,
//                                bpm = a.substringAfter("_BPM").replace(".wav", "").toFloat(),
//                                file = assetsManager.openFd("SpaceGypsiesBand/$a")
//                            ),
//                            viewModelScope,
//                            startPlaying = { bpm, mc ->
//                                if (masterBPM == 0f) {
//                                    masterBPM = bpm
//                                    sounds.forEach { smc ->
//                                        smc.changeBPM(masterBPM / smc.bpm)
//                                    }
//                                    masterMC = mc
//                                }
//                            },
//                            getMaster = {
//                                masterMC
//                            }
//                        )
//                    )
//                } catch (e: Exception) {
//                    println("ERROR FOR $a ***")
//                    println(e)
//                }
//            }
//        }
//    }
//}
//
//class MelodyController(
//    val melody: Melody,
//    private val viewModelScope: CoroutineScope,
//    private val startPlaying: (bpm: Float, mc: MelodyController) -> Unit,
//    private val getMaster: () -> MelodyController?,
//) {
//    var adjustedBPM by mutableStateOf(0f)
//    val bpm by mutableStateOf(melody.bpm)
//    val loop = CoroutineScope(viewModelScope.coroutineContext)
//    var currenSeek by mutableStateOf(0)
//    var launch: Job? = null
//    var total by mutableStateOf(0)
//
//    private val mediaPlayer = MediaPlayer()
//
//    init {
//        mediaPlayer.setDataSource(melody.file)
//        mediaPlayer.isLooping = true
//        mediaPlayer.prepare()
//    }
//
//    fun play() = viewModelScope.launch(Dispatchers.IO) {
//        startPlaying(bpm, this@MelodyController)
//        getMaster()?.currenSeek?.apply {
//            mediaPlayer.seekTo(this)
//        }
//        total = mediaPlayer.duration
//        mediaPlayer.start()
//        launch = loop.launch {
//            while (true) {
//                if (this.isActive) {
//                    delay(100L) // Delay for 0.1 second
//                    currenSeek = mediaPlayer.currentPosition
//                }
//            }
//        }
//    }
//
//    fun pause() = viewModelScope.launch(Dispatchers.IO) {
//        mediaPlayer.stop()
//        mediaPlayer.prepare()
//        launch?.cancelAndJoin()
//    }
//
//    fun changeBPM(newBPM: Float) {
//        if (newBPM > 0) {
//            adjustedBPM = bpm * newBPM
//            mediaPlayer.playbackParams.apply {
//                speed = newBPM
//                mediaPlayer.playbackParams = this
//            }
//            mediaPlayer.pause()
//        }
//    }
//
//    fun close() {
//        mediaPlayer.reset()
//        mediaPlayer.release()
//    }
//}
//
//data class Melody(
//    val id: Int = Random.nextInt(),
//    val title: String,
//    val file: AssetFileDescriptor,
//    val bpm: Float
//)