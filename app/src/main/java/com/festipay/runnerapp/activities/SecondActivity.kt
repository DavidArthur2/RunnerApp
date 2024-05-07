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
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.fragments.DemolitionFragment
import com.festipay.runnerapp.fragments.InstallFragment
import com.festipay.runnerapp.fragments.InventoryFragment
import com.festipay.runnerapp.fragments.ProgramSelectorFragment
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second)

        navigationViewListener()

        launchProgramSelector()

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        when (CurrentState.fragmentType) {
            in listOf(FragmentType.INSTALL, FragmentType.INVENTORY, FragmentType.DEMOLITION) -> {
                showLoadingScreen(this)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, ProgramSelectorFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            FragmentType.INVENTORY_ITEM_ADD ->{
                showLoadingScreen(this)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, InventoryFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }

            FragmentType.PROGRAM -> setupLogoutDialog()

            else -> setupLogoutDialog()
        }
    }

    private fun setupLogoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.exit_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.custom_error_button_bg))
        dialog.findViewById<TextView>(R.id.custom_dialog_title).text = getString(R.string.logoutText)
        dialog.findViewById<TextView>(R.id.exit_dialog_message_label).text = getString(R.string.logoutConfirmText)
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<Button>(R.id.back_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.exit_button).setOnClickListener {
            launchMain()
        }
    }

    private fun launchMain() {
        val nextActivity = Intent(this, MainActivity::class.java)
        startActivity(nextActivity)
        finish()
    }

    private fun launchProgramSelector(){
        val programSelectorFragment = ProgramSelectorFragment()
        supportFragmentManager.beginTransaction()
            //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            .replace(R.id.frameLayout, programSelectorFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
    }

    private fun navigationViewListener(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: androidx.fragment.app.Fragment = when (item.itemId) {
                R.id.install -> {
                    InstallFragment()
                }

                R.id.demolition -> {
                    DemolitionFragment()
                }

                R.id.inventory -> {
                    InventoryFragment()
                }

                else -> InstallFragment()
            }
            showLoadingScreen(this)
            supportFragmentManager.beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.frameLayout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()

            true
        }
    }
}
