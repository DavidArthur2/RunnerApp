package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Visibility
import com.festipay.runnerapp.R
import com.festipay.runnerapp.fragments.ProgramSelectorFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second)
        val appBar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        appBar.title = getString(R.string.program_selector_title)

        val b = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        b.isVisible = false
        val programSelectorFragment = ProgramSelectorFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, programSelectorFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

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