package com.festipay.runnerapp.utilities

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.fragment.app.FragmentActivity
import com.festipay.runnerapp.R
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class CamScanner(
    private val context: FragmentActivity,
    private val handleBarcode: (String) -> Unit
) {
    private lateinit var mediaPlayer: MediaPlayer

    init {
        initBeeper()
    }

    fun scanCode() {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                playBeep()
                handleBarcode(barcode.rawValue ?: "")
                scanCode()
            }
            .addOnCanceledListener {}
            .addOnFailureListener { e ->
                showError(context, "Barcode beolvasás sikertelen!", e.toString())
            }
    }

    private fun initBeeper() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < 70) audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            70,
            0
        )
        mediaPlayer = MediaPlayer.create(context, R.raw.beep)
    }

    private fun playBeep() {
        mediaPlayer.start()
    }
}