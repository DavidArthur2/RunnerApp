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

    //Inventory View-ek
    private lateinit var targyNevInput: EditText
    private lateinit var darabSzamInput: EditText
    private lateinit var snSwitch: SwitchMaterial
    private lateinit var modifyButton: Button

    //Install View-ek
    private lateinit var companyInstallInput: EditText
    private lateinit var kiadva: SwitchMaterial
    private lateinit var nemKirakhato: SwitchMaterial
    private lateinit var kirakva: SwitchMaterial
    private lateinit var eloszto: SwitchMaterial
    private lateinit var aram: SwitchMaterial
    private lateinit var szoftver: SwitchMaterial
    private lateinit var param: SwitchMaterial
    private lateinit var teszt: SwitchMaterial

    //Demolition View-ek
    private lateinit var companyDemolitionInput: EditText
    private lateinit var eszkozSzamInput: EditText
    private lateinit var folyamatban: SwitchMaterial
    private lateinit var csomagolt: SwitchMaterial
    private lateinit var autoban: SwitchMaterial
    private lateinit var bazisLeszereles: SwitchMaterial

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
        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)

        val mode: String = when(CurrentState.mode){
            Mode.INVENTORY -> {
                CurrentState.fragmentType = FragmentType.INVENTORY_ITEM_STATUS
                getString(R.string.inventory_string)

            }
            Mode.INSTALL -> {
                CurrentState.fragmentType = FragmentType.INSTALL_COMPANY_STATUS
                getString(R.string.install_string)
            }
            Mode.DEMOLITION -> {
                CurrentState.fragmentType = FragmentType.DEMOLITION_COMPANY_STATUS
                getString(R.string.demolition_string)
            }
            else -> getString(R.string.inventory_string)
        }

        if(appBar.title.length < 21)appBar.title =
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
            Mode.INSTALL -> {
                Database.db.collection("telephely_telepites").document(CurrentState.companySiteID ?: "").get()
                    .addOnSuccessListener { documents ->
                        companyInstallInput.setText(documents.data?.get("TelephelyNev") as String)
                        kiadva.isChecked = documents.data?.get("Kiadva") as Boolean
                        nemKirakhato.isChecked = documents.data?.get("NemKirakhato") as Boolean
                        kirakva.isChecked = documents.data?.get("Kirakva") as Boolean
                        eloszto.isChecked = documents.data?.get("Eloszto") as Boolean
                        aram.isChecked = documents.data?.get("Aram") as Boolean
                        szoftver.isChecked = documents.data?.get("Szoftver") as Boolean
                        param.isChecked = documents.data?.get("Param") as Boolean
                        teszt.isChecked = documents.data?.get("Teszt") as Boolean
                        onViewLoaded()
                    }
            }
            Mode.DEMOLITION -> {
                Database.db.collection("telephely_bontas").document(CurrentState.companySiteID ?: "").get()
                    .addOnSuccessListener { documents ->
                        companyDemolitionInput.setText(documents.data?.get("TelephelyNev") as String)
                        eszkozSzamInput.setText((documents.data?.get("Eszkozszam") as Long).toString())
                        folyamatban.isChecked = documents.data?.get("Folyamatban") as Boolean
                        csomagolt.isChecked = documents.data?.get("Csomagolt") as Boolean
                        autoban.isChecked = documents.data?.get("Autoban") as Boolean
                        bazisLeszereles.isChecked = documents.data?.get("BazisLeszereles") as Boolean
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
            Mode.INSTALL -> {
                companyInstallInput = view.findViewById(R.id.companyInstallNevInput)
                kiadva = view.findViewById(R.id.installFirstSwitch)
                nemKirakhato = view.findViewById(R.id.installSecondSwitch)
                kirakva = view.findViewById(R.id.installThirdSwitch)
                eloszto = view.findViewById(R.id.installFourthSwitch)
                aram = view.findViewById(R.id.installFifthSwitch)
                szoftver = view.findViewById(R.id.installSixthSwitch)
                param = view.findViewById(R.id.installSeventhSwitch)
                teszt = view.findViewById(R.id.installEightSwitch)
                modifyButton = view.findViewById(R.id.modifyExitButton)

                loadValues()

                modifyButton.setOnClickListener {
                    modifyButtonListener(
                        companyInstallItem = CompanyInstall(
                            companyInstallInput.text.toString(),
                            kiadva.isChecked,
                            nemKirakhato.isChecked,
                            kirakva.isChecked,
                            eloszto.isChecked,
                            aram.isChecked,
                            szoftver.isChecked,
                            param.isChecked,
                            teszt.isChecked,
                            null,
                            null,
                            null
                        )
                    )
                }
            }
            Mode.DEMOLITION -> {
                companyDemolitionInput = view.findViewById(R.id.companyDemolitionNevInput)
                eszkozSzamInput = view.findViewById(R.id.eszkozSzamInput)
                folyamatban = view.findViewById(R.id.demolitionFirstSwitch)
                csomagolt = view.findViewById(R.id.demolitionSecondSwitch)
                autoban = view.findViewById(R.id.demolitionThirdSwitch)
                bazisLeszereles = view.findViewById(R.id.demolitionFourthSwitch)
                modifyButton = view.findViewById(R.id.modifyExitButton)
                loadValues()

                modifyButton.setOnClickListener {
                    modifyButtonListener(
                        companyDemolitionItem = CompanyDemolition(
                            companyDemolitionInput.text.toString(),
                            eszkozSzamInput.text.toString().toIntOrNull() ?: 0,
                            folyamatban.isChecked,
                            csomagolt.isChecked,
                            autoban.isChecked,
                            bazisLeszereles.isChecked,
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
        if (eszkozSzamInput.text.isEmpty()) {
            showError(requireActivity(), "Adj meg eszközszámot!")
            return
        }
        if (companyDemolitionItem.telephelyNev.length < 3) {
            showError(requireActivity(), "A Telephely legalább 3 hosszú kell legyen!")
            return
        }
        val docID = CurrentState.companySiteID ?: ""
        val data = hashMapOf<String, Any>(
            "TelephelyNev" to companyDemolitionItem.telephelyNev,
            "Eszkozszam" to companyDemolitionItem.eszkozszam,
            "Folyamatban" to companyDemolitionItem.folyamatban,
            "Csomagolt" to companyDemolitionItem.csomagolt,
            "Autoban" to companyDemolitionItem.autoban,
            "BazisLeszereles" to companyDemolitionItem.bazisLeszereles,
        )
        Database.db.collection("telephely_bontas").document(docID).update(data).addOnSuccessListener {
            launchFragment(requireActivity(), DemolitionFragment())
            showInfoDialog(
                requireActivity(),
                "Módosítás",
                "Telephely sikeresen módosítva!",
                "Vissza"
            )
            logToFile("Updated: companysitename: ${companyDemolitionItem.telephelyNev} programname: ${CurrentState.programName} docid: $docID") }
    }

    private fun modifyCompanyInstall(companyInstallItem: CompanyInstall) {
        if (companyInstallItem.telephelyNev.length < 3) {
            showError(requireActivity(), "A Telephely legalább 3 hosszú kell legyen!")
            return
        }
        val docID = CurrentState.companySiteID ?: ""
        val data = hashMapOf<String, Any>(
            "TelephelyNev" to companyInstallItem.telephelyNev,
            "Kiadva" to companyInstallItem.kiadva,
            "NemKirakhato" to companyInstallItem.nemKirakhato,
            "Kirakva" to companyInstallItem.kirakva,
            "Eloszto" to companyInstallItem.eloszto,
            "Aram" to companyInstallItem.aram,
            "Szoftver" to companyInstallItem.szoftver,
            "Param" to companyInstallItem.param,
            "Teszt" to companyInstallItem.teszt
        )
        Database.db.collection("telephely_telepites").document(docID).update(data).addOnSuccessListener {
            launchFragment(requireActivity(), InstallFragment())
            showInfoDialog(
                requireActivity(),
                "Módosítás",
                "Telephely sikeresen módosítva!",
                "Vissza"
            )
            logToFile("Updated: companysitename: ${companyInstallItem.telephelyNev} programname: ${CurrentState.programName} docid: $docID")
        }
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
            launchFragment(requireActivity(), InventoryFragment())
            showInfoDialog(
                requireActivity(),
                "Módosítás",
                "Tárgy sikeresen módosítva!",
                "Vissza"
            )
            logToFile("Updated: itemname: ${inventoryItem.targyNev} programname: ${CurrentState.programName} docid: $docID")
        }
    }
}