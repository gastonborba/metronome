package rocks.borbit.metronome_core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource

@Composable
fun MetronomeScreen(
    onStart: (Int) -> Unit,
    onStop: () -> Unit
) {
    var bpm by remember { mutableStateOf(120) }
    var isRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("BPM: $bpm", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = bpm.toFloat(),
            onValueChange = { newValue ->
                bpm = newValue.toInt()
            },
            onValueChangeFinished = {
                if (isRunning) {
                    onStart(bpm)
                }
            },
            valueRange = 40f..240f,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (bpm > 40) {
                        bpm--
                        if (isRunning) onStart(bpm)
                    }
                },
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_minus),
                    contentDescription = stringResource(R.string.decrease_bpm)
                )
            }

            Button(
                onClick = {
                    if (isRunning) {
                        onStop()
                    } else {
                        onStart(bpm)
                    }
                    isRunning = !isRunning
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.size(80.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (isRunning) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (isRunning) stringResource(R.string.pause) else stringResource(R.string.play),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = {
                    if (bpm < 240) {
                        bpm++
                        if (isRunning) onStart(bpm)
                    }
                },
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = stringResource(R.string.increase_bpm)
                )
            }
        }
    }
}
