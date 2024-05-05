package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CurrentState
import com.festipay.runnerapp.fragments.DemolitionFragment
import com.festipay.runnerapp.fragments.InstallFragment
import com.festipay.runnerapp.fragments.ProgramSelectorFragment
import com.festipay.runnerapp.utilities.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: androidx.fragment.app.Fragment = when (item.itemId) {
                R.id.install -> {
                    InstallFragment()
                }

                R.id.demolition -> {
                    DemolitionFragment()
                }

                else -> InstallFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            true
        }

        val programSelectorFragment = ProgramSelectorFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, programSelectorFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        when(CurrentState.fragment){
            in listOf(Fragment.INSTALL, Fragment.INVENTORY, Fragment.DEMOLITION) ->{
                val programSelectorFragment = ProgramSelectorFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, programSelectorFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }

            Fragment.PROGRAM -> setupLogoutDialog("Kijelentkezés", "Biztosan ki szeretnél jelentkezni?")
            else -> setupLogoutDialog("Kijelentkezés", "Biztosan ki szeretnél jelentkezni?")
        }
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
