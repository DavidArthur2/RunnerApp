package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.viewmodel.viewModelFactory
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
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
        val view = inflater.inflate(R.layout.fragment_status_modify_inventory, container, false)

        initFragment()

        onViewLoaded()
        return view
    }

    private fun initFragment(){
        CurrentState.operation = OperationType.STATUS_MODIFY
        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)
        val mode: String?
        if(CurrentState.mode == Mode.INVENTORY)
            mode = getString(R.string.inventory_string)
        else
            mode = getString(R.string.inventory_string)

        appBar.title =
            "${CurrentState.programName} - $mode - ${CurrentState.companySite} - Módosítás"
    }
    fun initViews(){
        when(CurrentState.mode) {
            Mode.INVENTORY -> {
                targyNevInput = requireActivity().findViewById(R.id.targyNevInput)
                darabSzamInput = requireActivity().findViewById(R.id.darabSzamInput)
                snSwitch = requireActivity().findViewById(R.id.snSwitch)
                modifyButton = requireActivity().findViewById(R.id.modifyExitButton)

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

    private fun modifyButtonListener(inventoryItem: Inventory? = null, companyInstallItem: CompanyInstall? = null, companyDemolitionItem: CompanyDemolition? = null) {
        Functions.showLoadingScreen(requireActivity())
        if(inventoryItem != null)
            return modifyInventory(inventoryItem)
        if(companyInstallItem != null)
            return modifyCompanyInstall(companyInstallItem)
        if(companyDemolitionItem != null)
            return modifyCompanyDemolition(companyDemolitionItem)
    }

    private fun onViewLoaded(){
        Functions.hideLoadingScreen()
    }

    private fun modifyCompanyDemolition(companyDemolitionItem: CompanyDemolition){

    }
    private fun modifyCompanyInstall(companyInstallItem: CompanyInstall){

    }
    private fun modifyInventory(inventoryItem: Inventory){

        if (darabSzamInput.text.isEmpty()) {
            showError(requireActivity(), "Adj meg darabszámot!")
            return
        }
        if (inventoryItem.targyNev.length < 3) {
            showError(requireActivity(), "A tárgynév legalább 3 hosszú kell legyen!")
            return
        }

        Database.db.collection("leltar").whereEqualTo("TargyNev", CurrentState.companySite)
            .whereEqualTo("Program", CurrentState.programName).get().addOnSuccessListener {
                if (it.isEmpty)
                    addData(item, comment, exit = true)
                else
                    showError(
                        requireActivity(),
                        "Már létezik ilyen tárgynév ezen a programon!",
                        "itemname: ${item.targyNev} programname: ${CurrentState.programName}"
                    )
            }
    }
}