package loops.ui.screens

import android.view.MotionEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    SampleView(fileName = viewModel.fileOnIndex(0)) {
                        if (it) viewModel.triggerDown(0) else viewModel.triggerUp(0)
                    }
                    SampleView(fileName = viewModel.fileOnIndex(1)) {
                        if (it) viewModel.triggerDown(1) else viewModel.triggerUp(1)
                    }
                    SampleView(fileName = viewModel.fileOnIndex(2)) {
                        if (it) viewModel.triggerDown(2) else viewModel.triggerUp(2)
                    }
                    SampleView(fileName = viewModel.fileOnIndex(3)) {
                        if (it) viewModel.triggerDown(3) else viewModel.triggerUp(3)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    SampleView(fileName = viewModel.fileOnIndex(4)) {
                        if (it) viewModel.triggerDown(4) else viewModel.triggerUp(4)
                    }
                    SampleView(fileName = viewModel.fileOnIndex(5)) {
                        if (it) viewModel.triggerDown(5) else viewModel.triggerUp(5)
                    }
                    SampleView(fileName = viewModel.fileOnIndex(6)) {
                        if (it) viewModel.triggerDown(6) else viewModel.triggerUp(6)
                    }
                    SampleView(fileName = viewModel.fileOnIndex(7)) {
                        if (it) viewModel.triggerDown(7) else viewModel.triggerUp(7)
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
    onStateChange: (boolean: Boolean) -> Unit
) {
    var checkedStateToggle by remember {
        mutableStateOf(false)
    }
    var checkedStateHold by remember {
        mutableStateOf(false)
    }
    Column {
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
    }
}