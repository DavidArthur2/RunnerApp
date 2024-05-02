package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.festipay.runnerapp.R
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.BarcodeScanReceiver
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.system.exitProcess

class ProgramSelectorActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_program_selector)

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        setupLogoutDialog("Kijelentkezés", "Biztosan ki szeretnél jelentkezni?")
    }

    fun setupLogoutDialog(title:String, message:String){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.exit_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.custom_error_button_bg))
        dialog.findViewById<TextView>(R.id.custom_dialog_title).text = title
        dialog.findViewById<TextView>(R.id.exit_dialog_message_label).text = message
        dialog.setCancelable(false)
        dialog.show()
        dialog.findViewById<Button>(R.id.back_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.exit_button).setOnClickListener{
            launchMain()
        }
    }
    private fun launchMain(){
        val nextActivity = Intent(this, MainActivity::class.java)
        startActivity(nextActivity)
        finish()
    }
}