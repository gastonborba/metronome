package rocks.borbit.metronome_core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin
import android.content.Context
import android.util.Log
import java.io.BufferedInputStream
import java.io.DataInputStream


class Metronome(
    private val context: Context,
    private val onBeat: (Int) -> Unit
) {
    private var bpm = 120
    private var beatsPerMeasure = 4
    private var isPlaying = false
    private val sampleRate = 44100

    private var audioTrack: AudioTrack? = null
    private var audioBuffer: ShortArray = shortArrayOf()
    private var clickSample: ShortArray = shortArrayOf()

    init {
        // Cargar WAV al iniciar
        clickSample = loadWavFromRaw(R.raw.tap_click)
    }

    fun setBpm(newBpm: Int) {
        bpm = newBpm
        if (isPlaying) {
            stop()
            start()
        }
    }

    fun start() {
        if (isPlaying) return
        isPlaying = true

        audioBuffer = generatePattern()

        val bufferSizeBytes = audioBuffer.size * 2

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSizeBytes)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack?.write(audioBuffer, 0, audioBuffer.size)
        audioTrack?.setLoopPoints(0, audioBuffer.size, -1)
        audioTrack?.play()
    }

    fun stop() {
        isPlaying = false
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    private fun generatePattern(): ShortArray {
        val secondsPerBeat = 60.0 / bpm
        val samplesPerBeat = (sampleRate * secondsPerBeat).toInt()
        val totalSamples = samplesPerBeat * beatsPerMeasure

        val buffer = ShortArray(totalSamples) { 0 }

        for (beat in 0 until beatsPerMeasure) {
            val startIndex = beat * samplesPerBeat
            // Copiar clickSample dentro del buffer en la posición correspondiente
            for (i in clickSample.indices) {
                val pos = startIndex + i
                if (pos < buffer.size) {
                    buffer[pos] = clickSample[i]
                }
            }
        }

        return buffer
    }

    private fun loadWavFromRaw(resId: Int): ShortArray {
        try {
            context.resources.openRawResource(resId).use { inputStream ->
                val dis = DataInputStream(BufferedInputStream(inputStream))

                // Saltar cabecera WAV (44 bytes estándar)
                val header = ByteArray(44)
                dis.readFully(header)

                val pcmData = mutableListOf<Short>()
                while (dis.available() > 1) {
                    val low = dis.readUnsignedByte()
                    val high = dis.readUnsignedByte()
                    val sample = (high shl 8) or low
                    pcmData.add(sample.toShort())
                }
                return pcmData.toShortArray()
            }
        } catch (e: Exception) {
            Log.e("Metronome", "Error loading wav", e)
            // fallback: sonido simple si falla
            return generateFallbackClick()
        }
    }

    private fun generateFallbackClick(): ShortArray {
        val durationMs = 50
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val buffer = ShortArray(numSamples)
        val freq = 1000.0

        for (i in 0 until numSamples) {
            val value = (sin(2.0 * PI * i * freq / sampleRate) * Short.MAX_VALUE).toInt()
            buffer[i] = value.toShort()
        }
        return buffer
    }
}

