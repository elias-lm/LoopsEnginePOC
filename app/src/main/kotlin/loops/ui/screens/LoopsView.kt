package loops.ui.screens

import android.view.MotionEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import liv.eli.loops.R

@Composable
fun LoopsView(viewModel: LoopsViewModel) {
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(onClick = {
            /*TODO*/
        }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = "Add Sound" //TODO
            )
        }
    }) { pd ->
        Surface(modifier = Modifier.padding(pd)) {
            val currentMaster by remember {
                viewModel.currentMaster
            }
            val masterFrame by remember {
                viewModel.masterFrame
            }
            Column {
                Column(modifier = Modifier.fillMaxWidth(), Arrangement.Center) {
                    Text(text = "$currentMaster")
                    Text(text = "$masterFrame")
                }
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f)) {
                        for (index in 0..3) {
                            SampleView(
                                fileName = viewModel.fileNameForIndex(index),
                                { viewModel.getMaxFrames(index) },
                                { viewModel.getCurrentFrame(index) }) {
                                if (it) viewModel.triggerDown(index) else viewModel.triggerUp(index)
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        for (index in 4..7) {
                            SampleView(
                                fileName = viewModel.fileNameForIndex(index),
                                { viewModel.getMaxFrames(index) },
                                { viewModel.getCurrentFrame(index) }) {
                                if (it) viewModel.triggerDown(index) else viewModel.triggerUp(index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SampleView(
    fileName: String,
    getMaxFrames: () -> Int,
    getCurrentFrame: () -> Int,
    onStateChange: (boolean: Boolean) -> Unit
) {
    var checkedStateToggle by remember {
        mutableStateOf(false)
    }
    var checkedStateHold by remember {
        mutableStateOf(false)
    }
    var currentFrame by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = fileName, block = {
        scope.launch {
            while (true) {
                // Fetch data
                delay(100)
                currentFrame = getCurrentFrame()
            }
        }
    })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, color = Color.DarkGray)
    ) {
        Text(text = fileName)
        Row {
            IconToggleButton(
                checked = checkedStateToggle,
                onCheckedChange = { checkedStateToggle = it }) {
                val tint by animateColorAsState(
                    targetValue = if (checkedStateToggle) Color.Magenta else Color.LightGray
                )

                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = "Toggle Item",
                    modifier = Modifier
                        .clickable {
                            checkedStateToggle = !checkedStateToggle
                            onStateChange(checkedStateToggle)
                        }
                        .size(48.dp),
                    tint = tint
                )
            }
            Box(modifier = Modifier.pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onStateChange(true)
                        checkedStateHold = true
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        onStateChange(false)
                        checkedStateHold = false
                        true
                    }

                    else -> false
                }
            }) {
                val tint by animateColorAsState(
                    targetValue = if (checkedStateHold) Color.Magenta else Color.LightGray
                )

                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Hold Item",
                    modifier = Modifier.size(48.dp),
                    tint = tint
                )
            }
        }
        Text(text = "F: $currentFrame / ${getMaxFrames()}")
    }
}