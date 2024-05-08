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
import com.festipay.runnerapp.fragments.OperationSelectorFragment
import com.festipay.runnerapp.fragments.ProgramSelectorFragment
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_second)

        navigationViewListener()

        launchFragment(this, ProgramSelectorFragment())

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        when (CurrentState.fragmentType) {
            in listOf(FragmentType.INSTALL, FragmentType.INVENTORY, FragmentType.DEMOLITION) ->
                launchFragment(this, ProgramSelectorFragment())

            FragmentType.INVENTORY_ITEM_ADD, FragmentType.INVENTORY_ITEM ->
                launchFragment(this, InventoryFragment())

            FragmentType.INVENTORY_ITEM_STATUS ->
                launchFragment(this, OperationSelectorFragment())

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
    private fun navigationViewListener(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.install ->
                    launchFragment(this, InstallFragment())


                R.id.demolition ->
                    launchFragment(this, DemolitionFragment())


                R.id.inventory ->
                    launchFragment(this, InventoryFragment())

                else -> launchFragment(this, InstallFragment())
            }
            true
        }
    }
}
