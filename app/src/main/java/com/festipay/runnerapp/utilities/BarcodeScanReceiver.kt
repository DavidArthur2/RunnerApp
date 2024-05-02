package com.festipay.runnerapp.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.festipay.runnerapp.utilities.showError

class BarcodeScanReceiver(private val listener: (String?) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.ACTION_DECODE_DATA") {
            try {
                val decodedData =
                    intent.getStringExtra("barcode_string") ?: throw Exception("Barcode read null")
                listener.invoke(decodedData)
            } catch (ex: Exception) {
                showError(context, ex.toString())
            }
        }
    }
}