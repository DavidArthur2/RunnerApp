package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.festipay.runnerapp.R
import com.festipay.runnerapp.fragments.CommentsFragment
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.fragments.DemolitionFragment
import com.festipay.runnerapp.fragments.InstallFragment
import com.festipay.runnerapp.fragments.InventoryFragment
import com.festipay.runnerapp.fragments.OperationSelectorFragment
import com.festipay.runnerapp.fragments.ProgramSelectorFragment
import com.festipay.runnerapp.fragments.SNAddFragment
import com.festipay.runnerapp.fragments.StatusModifyFragment
import com.festipay.runnerapp.utilities.Dialogs.Companion.showLogoutDialog
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.OperationType
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()
        fragmentDecider()
    }

    private fun initActivity() {
        setContentView(R.layout.activity_second)
        val appBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(appBar)
        navigationViewListener()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        when (CurrentState.fragmentType) {
            in listOf(
                FragmentType.INSTALL,
                FragmentType.INVENTORY,
                FragmentType.DEMOLITION,
                FragmentType.FINAL_INVENTORY
            ) ->
                launchFragment(this, ProgramSelectorFragment())

            FragmentType.INVENTORY_ITEM_ADD, FragmentType.INVENTORY_ITEM ->
                launchFragment(this, InventoryFragment())

            FragmentType.FINAL_INVENTORY_ITEM_ADD, FragmentType.FINAL_INVENTORY_ITEM -> {
                launchFragment(this, InventoryFragment(), true)
            }

            FragmentType.INVENTORY_ITEM_STATUS, FragmentType.INSTALL_COMPANY_STATUS, FragmentType.DEMOLITION_COMPANY_STATUS, FragmentType.FINAL_INVENTORY_ITEM_STATUS -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
                if (fragment is StatusModifyFragment)
                    fragment.onBackCalled()
            }

            FragmentType.DEMOLITION_COMPANY_COMMENTS, FragmentType.INSTALL_COMPANY_COMMENTS, FragmentType.INVENTORY_ITEM_COMMENTS, FragmentType.FINAL_INVENTORY_ITEM_COMMENTS,
            FragmentType.DEMOLITION_COMPANY_SN, FragmentType.INSTALL_COMPANY_SN, FragmentType.INVENTORY_ITEM_SN, FragmentType.FINAL_INVENTORY_ITEM_SN,
            FragmentType.INSTALL_COMPANY_GPS, FragmentType.DEMOLITION_COMPANY_CAMERA ->
                launchFragment(this, OperationSelectorFragment())

            FragmentType.INSTALL_COMPANY ->
                launchFragment(this, InstallFragment())

            FragmentType.DEMOLITION_COMPANY ->
                launchFragment(this, DemolitionFragment())

            FragmentType.DEMOLITION_COMPANY_COMMENTS_ADD, FragmentType.INSTALL_COMPANY_COMMENTS_ADD, FragmentType.INVENTORY_ITEM_COMMENTS_ADD, FragmentType.FINAL_INVENTORY_ITEM_COMMENTS_ADD ->
                launchFragment(this, CommentsFragment())

            FragmentType.DEMOLITION_COMPANY_SN_ADD, FragmentType.INSTALL_COMPANY_SN_ADD, FragmentType.INVENTORY_ITEM_SN_ADD, FragmentType.FINAL_INVENTORY_ITEM_SN_ADD -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
                if (fragment is SNAddFragment)
                    fragment.onBackCalled()
            }

            FragmentType.DEMOLITION_COMPANY_SN_ADD_INSTANT ->
                launchFragment(this, OperationSelectorFragment())

            FragmentType.PROGRAM -> showLogoutDialog(this, ::launchMain)

            else -> showLogoutDialog(this, ::launchMain)
        }
    }

    private fun launchMain() {
        val nextActivity = Intent(this, MainActivity::class.java)
        startActivity(nextActivity)
        finish()
    }

    private fun navigationViewListener() {
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

    private fun fragmentDecider() {  // Akkora ha GPS viewből lép vissza
        val bottomView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if (CurrentState.operation != OperationType.GPS)
            launchFragment(this, ProgramSelectorFragment())
        else
            when (CurrentState.fragmentType) {
                FragmentType.INSTALL_COMPANY_GPS -> bottomView.selectedItemId = R.id.install
                FragmentType.DEMOLITION_COMPANY_GPS -> bottomView.selectedItemId = R.id.demolition
                else -> launchFragment(this, ProgramSelectorFragment())
            }
    }

}
