package rocks.borbit.metronome_core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.os.Build

class MetronomeService : Service() {
    private lateinit var metronome: Metronome
    private var isRunning = false
    private var iconRes: Int = android.R.drawable.ic_media_play

    override fun onCreate() {
        super.onCreate()
        metronome = Metronome(this) { /* Callback */ }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bpm = intent?.getIntExtra("bpm", 120) ?: 120
        iconRes = intent?.getIntExtra("iconRes", android.R.drawable.ic_media_play)
            ?: android.R.drawable.ic_media_play

        createNotificationChannel()
        startForeground(1, buildNotification(iconRes))

        if (isRunning) {
            metronome.setBpm(bpm)
        } else {
            metronome.setBpm(bpm)
            metronome.start()
            isRunning = true
        }

        return START_STICKY
    }

    override fun onDestroy() {
        metronome.stop()
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "metronome_channel",
                "Metronome",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(iconRes: Int): Notification {
        return NotificationCompat.Builder(this, "metronome_channel")
            .setContentTitle(getString(R.string.metronome_running))
            .setSmallIcon(iconRes)
            .build()
    }
}
