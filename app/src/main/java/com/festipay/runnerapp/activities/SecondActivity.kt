package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.festipay.runnerapp.R
import com.festipay.runnerapp.fragments.CommentsFragment
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.fragments.DemolitionFragment
import com.festipay.runnerapp.fragments.InstallFragment
import com.festipay.runnerapp.fragments.InventoryFragment
import com.festipay.runnerapp.fragments.OperationSelectorFragment
import com.festipay.runnerapp.fragments.ProgramSelectorFragment
import com.festipay.runnerapp.fragments.SNAddFragment
import com.festipay.runnerapp.fragments.SNFragment
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.system.exitProcess

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
            in listOf(FragmentType.INSTALL, FragmentType.INVENTORY, FragmentType.DEMOLITION, FragmentType.FINAL_INVENTORY) ->
                launchFragment(this, ProgramSelectorFragment())

            FragmentType.INVENTORY_ITEM_ADD, FragmentType.INVENTORY_ITEM ->
                launchFragment(this, InventoryFragment())

            FragmentType.FINAL_INVENTORY_ITEM_ADD, FragmentType.FINAL_INVENTORY_ITEM ->{
                launchFragment(this, InventoryFragment(), true)
            }

            FragmentType.INVENTORY_ITEM_STATUS, FragmentType.INSTALL_COMPANY_STATUS, FragmentType.DEMOLITION_COMPANY_STATUS, FragmentType.FINAL_INVENTORY_ITEM_STATUS,
            FragmentType.DEMOLITION_COMPANY_COMMENTS, FragmentType.INSTALL_COMPANY_COMMENTS, FragmentType.INVENTORY_ITEM_COMMENTS, FragmentType.FINAL_INVENTORY_ITEM_COMMENTS,
            FragmentType.DEMOLITION_COMPANY_SN, FragmentType.INSTALL_COMPANY_SN, FragmentType.INVENTORY_ITEM_SN, FragmentType.FINAL_INVENTORY_ITEM_SN,
            FragmentType.INSTALL_COMPANY_GPS ->
                launchFragment(this, OperationSelectorFragment())

            FragmentType.INSTALL_COMPANY ->
                launchFragment(this, InstallFragment())

            FragmentType.DEMOLITION_COMPANY ->
                launchFragment(this, DemolitionFragment())

            FragmentType.DEMOLITION_COMPANY_COMMENTS_ADD, FragmentType.INSTALL_COMPANY_COMMENTS_ADD, FragmentType.INVENTORY_ITEM_COMMENTS_ADD, FragmentType.FINAL_INVENTORY_ITEM_COMMENTS_ADD ->
                launchFragment(this, CommentsFragment())

            FragmentType.DEMOLITION_COMPANY_SN_ADD, FragmentType.INSTALL_COMPANY_SN_ADD, FragmentType.INVENTORY_ITEM_SN_ADD, FragmentType.FINAL_INVENTORY_ITEM_SN_ADD -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
                if(fragment is SNAddFragment)
                    fragment.onBackCalled()
            }

            FragmentType.DEMOLITION_COMPANY_SN_ADD_INSTANT ->
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


                R.id.finalInventory -> {
                    launchFragment(this, InventoryFragment(), true)
                }

                else -> launchFragment(this, InstallFragment())
            }
            true
        }
    }

    override fun onPause() {
        if (!this.isFinishing){
            exitProcess(0)
        }
        super.onPause()
    }
}
