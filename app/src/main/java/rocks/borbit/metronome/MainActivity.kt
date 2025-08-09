package rocks.borbit.metronome

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import rocks.borbit.metronome.ui.theme.MetronomeTheme
import rocks.borbit.metronome_core.MetronomeScreen
import rocks.borbit.metronome_core.MetronomeService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetronomeTheme {
                Surface {
                    MetronomeScreen(
                        onStart = { bpm: Int ->
                            val intent = Intent(this, MetronomeService::class.java).apply {
                                putExtra("bpm", bpm)
                                putExtra("iconRes", R.drawable.ic_launcher_foreground)
                            }
                            startService(intent)
                        },
                        onStop = {
                            stopService(Intent(this, MetronomeService::class.java))
                        }
                    )
                }
            }
        }
    }
}