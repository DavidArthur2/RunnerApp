package com.festipay.runnerapp

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.festipay.runnerapp.R.*
import com.festipay.runnerapp.database.Database
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var barcodeScanReceiver: BarcodeScanReceiver

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeScanReceiver = BarcodeScanReceiver { barcode -> handleDecodeData(barcode) }
        setContentView(layout.activity_main)
        val button: Button = findViewById(id.button)
        Database

        button.setOnClickListener {
            var result: List<Map<String, Any>>? = null
            Database.getCompanyInstall()
            //Log.d("Res", result.toString())
        }
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleDecodeData(decodedData: String?) {
        Toast.makeText(this, "Kaptam $decodedData!", Toast.LENGTH_SHORT).show()


    }

}