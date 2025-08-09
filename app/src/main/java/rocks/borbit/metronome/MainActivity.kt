package rocks.borbit.metronome

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import rocks.borbit.metronome.ui.theme.MetronomeTheme
import rocks.borbit.metronome_core.MetronomeScreen
import rocks.borbit.metronome_core.MetronomeService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetronomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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