package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.viewModelFactory
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.logToFile
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class StatusModifyFragment : Fragment() {

    private lateinit var targyNevInput: EditText
    private lateinit var darabSzamInput: EditText
    private lateinit var snSwitch: SwitchMaterial
    private lateinit var modifyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = when(CurrentState.mode){
            Mode.INVENTORY -> inflater.inflate(R.layout.fragment_status_modify_inventory, container, false)
            Mode.INSTALL -> inflater.inflate(R.layout.fragment_status_modify_install, container, false)
            Mode.DEMOLITION -> inflater.inflate(R.layout.fragment_status_modify_demolition, container, false)
            else -> inflater.inflate(R.layout.fragment_status_modify_inventory, container, false)
        }

        initFragment(view)
        return view
    }

    private fun initFragment(view: View) {
        CurrentState.operation = OperationType.STATUS_MODIFY
        CurrentState.fragmentType = FragmentType.INVENTORY_ITEM_STATUS

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible = false
        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)

        val mode: String = when(CurrentState.mode){
            Mode.INVENTORY -> getString(R.string.inventory_string)
            Mode.INSTALL -> getString(R.string.install_string)
            Mode.DEMOLITION -> getString(R.string.demolition_string)
            else -> getString(R.string.inventory_string)
        }

        appBar.title =
            "${CurrentState.programName} - $mode - ${CurrentState.companySite} - Módosítás"

        initViews(view)
    }

    private fun loadValues(){
        when (CurrentState.mode) {
            Mode.INVENTORY -> {
                Database.db.collection("leltar").document(CurrentState.companySiteID ?: "").get()
                    .addOnSuccessListener { documents ->
                        targyNevInput.setText(documents.data?.get("TargyNev") as String)
                        darabSzamInput.setText((documents.data?.get("Darabszam") as Long).toString())
                        snSwitch.isChecked = documents.data?.get("SN") as Boolean
                        onViewLoaded()
                    }
            }
            else -> return
        }
    }

    private fun initViews(view: View) {
        when (CurrentState.mode) {
            Mode.INVENTORY -> {
                targyNevInput = view.findViewById(R.id.targyNevInput)
                darabSzamInput = view.findViewById(R.id.darabSzamInput)
                snSwitch = view.findViewById(R.id.snSwitch)
                modifyButton = view.findViewById(R.id.modifyExitButton)

                loadValues()

                modifyButton.setOnClickListener {
                    modifyButtonListener(
                        inventoryItem = Inventory(
                            darabSzamInput.text.toString().toIntOrNull() ?: 0,
                            snSwitch.isChecked,
                            targyNevInput.text.toString(),
                            null,
                            null,
                            null
                        )
                    )
                }
            }
            else -> return
        }

    }

    private fun modifyButtonListener(
        inventoryItem: Inventory? = null,
        companyInstallItem: CompanyInstall? = null,
        companyDemolitionItem: CompanyDemolition? = null
    ) {
        Functions.showLoadingScreen(requireActivity())
        if (inventoryItem != null)
            return modifyInventory(inventoryItem)
        if (companyInstallItem != null)
            return modifyCompanyInstall(companyInstallItem)
        if (companyDemolitionItem != null)
            return modifyCompanyDemolition(companyDemolitionItem)
    }

    private fun onViewLoaded() {
        Functions.hideLoadingScreen()
    }

    private fun modifyCompanyDemolition(companyDemolitionItem: CompanyDemolition) {

    }

    private fun modifyCompanyInstall(companyInstallItem: CompanyInstall) {

    }

    private fun modifyInventory(inventoryItem: Inventory) {

        if (darabSzamInput.text.isEmpty()) {
            showError(requireActivity(), "Adj meg darabszámot!")
            return
        }
        if (inventoryItem.targyNev.length < 3) {
            showError(requireActivity(), "A tárgynév legalább 3 hosszú kell legyen!")
            return
        }
        val docID = CurrentState.companySiteID ?: ""
        val data = hashMapOf<String, Any>(
            "Darabszam" to inventoryItem.darabszam,
            "SN" to inventoryItem.sn,
            "TargyNev" to inventoryItem.targyNev
        )
        Database.db.collection("leltar").document(docID).update(data).addOnSuccessListener {
            showInfoDialog(
                requireActivity(),
                "Módosítás",
                "Tárgy sikeresen módosítva!",
                "Vissza"
            )
            logToFile("Updated: itemname: ${inventoryItem.targyNev} programname: ${CurrentState.programName} docid: $docID")
            launchFragment(requireActivity(), InventoryFragment())
        }
    }
}