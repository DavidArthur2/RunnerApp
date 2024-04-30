package com.festipay.runnerapp

import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.festipay.runnerapp.R.*

class MainActivity : AppCompatActivity() {
    private lateinit var barcodeScanReceiver: BarcodeScanReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeScanReceiver = BarcodeScanReceiver { barcode -> handleDecodeData(barcode) }
        setContentView(layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("android.intent.ACTION_DECODE_DATA")
        registerReceiver(barcodeScanReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(barcodeScanReceiver)
    }

    private fun handleDecodeData(decodedData: String?) {
        Toast.makeText(this, "Kaptam $decodedData!", Toast.LENGTH_LONG).show()
    }

}