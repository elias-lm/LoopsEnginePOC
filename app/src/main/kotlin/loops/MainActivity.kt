package loops

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.microsoft.snippet.Snippet
import com.microsoft.snippet.Snippet.MeasuredExecutionPath
import kotlinx.coroutines.launch
import loops.ui.screens.RootScreen
import loops.ui.screens.RootScreenViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel


class MainActivity : ComponentActivity() {

    val loopsEngine: LoopsEngine by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Snippet.install(MeasuredExecutionPath())
        Snippet.newFilter("TemporalSnipper")
        Snippet.addFlag( Snippet.FLAG_METADATA_METHOD and Snippet.FLAG_METADATA_CLASS)
        val rootScreenViewModel: RootScreenViewModel by stateViewModel()
        setContent {
            RootScreen(rootScreenViewModel)
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            Snippet.capture {
                loopsEngine.setupAudioStream()
                loopsEngine.loadWavAssets()
                loopsEngine.startAudioStream()
            }
        }
    }

    override fun onStop() {
        loopsEngine.teardownAudioStream()
        loopsEngine.unloadWavAssets()
        super.onStop()
    }

}