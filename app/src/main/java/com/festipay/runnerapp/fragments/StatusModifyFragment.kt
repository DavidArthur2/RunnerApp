package com.festipay.runnerapp.fragments

import LocationGetter.getLocation
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.DemolitionFirstItemEnum
import com.festipay.runnerapp.data.DemolitionSecondItemEnum
import com.festipay.runnerapp.data.InstallFirstItemEnum
import com.festipay.runnerapp.data.InstallSecondItemEnum
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
import com.festipay.runnerapp.utilities.showWarning
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

class StatusModifyFragment : Fragment() {

    //Global View-ek
    private lateinit var modifyExitButton: Button
    private lateinit var modifyAddButton: Button
    private lateinit var commentInput : EditText

    //Inventory View-ek
    private lateinit var itemNameInput: EditText
    private lateinit var deviceNumberInput: EditText

    //Install View-ek
    private lateinit var pinGo: ImageView
    private lateinit var companyName: TextView
    private lateinit var firstItemI: Spinner
    private lateinit var secondItemI: Spinner
    private lateinit var thirdItemI: SwitchMaterial
    private lateinit var fourthItemI: SwitchMaterial
    private lateinit var fifthItemI: SwitchMaterial
    private lateinit var sixthItemI: SwitchMaterial
    private lateinit var seventhItemI: SwitchMaterial
    private lateinit var eightItemI: SwitchMaterial
    private lateinit var ninethItemI: SwitchMaterial

    //Demolition View-ek
    private lateinit var firstItemD: Spinner
    private lateinit var secondItemD: Spinner
    private lateinit var thirdItemD: SwitchMaterial

    private lateinit var modeName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = when(CurrentState.mode){
            Mode.INVENTORY -> inflater.inflate(R.layout.fragment_status_modify_inventory, container, false)
            Mode.INSTALL -> inflater.inflate(R.layout.fragment_status_modify_install, container, false)
            Mode.DEMOLITION -> inflater.inflate(R.layout.fragment_status_modify_demolition, container, false)
            Mode.FINAL_INVENTORY -> inflater.inflate(R.layout.fragment_status_modify_inventory, container, false)
            else -> inflater.inflate(R.layout.fragment_status_modify_inventory, container, false)
        }

        initFragment(view)
        return view
    }

    private fun initFragment(view: View) {
        CurrentState.operation = OperationType.STATUS_MODIFY
        modeName = Database.mapCollectionModeName()
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
            Mode.FINAL_INVENTORY -> {
                CurrentState.fragmentType = FragmentType.FINAL_INVENTORY_ITEM_STATUS
                "Záró leltár"
            }
            else -> getString(R.string.inventory_string)
        }

        appBar.title = "${CurrentState.companySite} - Módosítás"

        initViews(view)
    }

    private fun loadValues(){
        when (CurrentState.mode) {
            Mode.INVENTORY -> {
                Database.db.collection("Inventory").document(CurrentState.companySiteID ?: "").get()
                    .addOnSuccessListener { documents ->
                        itemNameInput.setText(documents.data?.get("ItemName") as String)
                        deviceNumberInput.setText((documents.data?.get("Quantity") as Long).toString())
                        onViewLoaded()
                    }
            }
            Mode.FINAL_INVENTORY -> {
                Database.db.collection("Final_Inventory").document(CurrentState.companySiteID ?: "").get()
                    .addOnSuccessListener { documents ->
                        itemNameInput.setText(documents.data?.get("ItemName") as String)
                        deviceNumberInput.setText((documents.data?.get("Quantity") as Long).toString())
                        onViewLoaded()
                    }
            }
            Mode.INSTALL -> {
                Database.db.collection("Company_Install").document(CurrentState.companySiteID ?: "").get()
                    .addOnSuccessListener { documents ->
                        companyName.text = (documents.data?.get("CompanyName") as String)
                        firstItemI.setSelection(InstallFirstItemEnum.valueOf(documents.data?.get("1") as String).ordinal)
                        secondItemI.setSelection(InstallSecondItemEnum.valueOf(documents.data?.get("2") as String).ordinal)
                        thirdItemI.isChecked = documents.data?.get("3") as Boolean
                        fourthItemI.isChecked = documents.data?.get("4") as Boolean
                        fifthItemI.isChecked = documents.data?.get("5") as Boolean
                        sixthItemI.isChecked = documents.data?.get("6") as Boolean
                        seventhItemI.isChecked = documents.data?.get("7") as Boolean
                        eightItemI.isChecked = documents.data?.get("8") as Boolean
                        ninethItemI.isChecked = documents.data?.get("9") as Boolean
                        onViewLoaded()
                    }
            }
            Mode.DEMOLITION -> {
                Database.db.collection("Company_Demolition").document(CurrentState.companySiteID ?: "").get()
                    .addOnSuccessListener { documents ->
                        companyName.text = (documents.data?.get("CompanyName") as String)
                        firstItemD.setSelection(DemolitionFirstItemEnum.valueOf(documents.data?.get("1") as String).ordinal)
                        secondItemD.setSelection(DemolitionSecondItemEnum.valueOf(documents.data?.get("2") as String).ordinal)
                        thirdItemD.isChecked = documents.data?.get("3") as Boolean
                        onViewLoaded()
                    }
            }
            else -> return
        }
    }

    @SuppressLint("CutPasteId")
    private fun initViews(view: View) {
        modifyAddButton = view.findViewById(R.id.modifyAddButton)
        modifyExitButton = view.findViewById(R.id.modifyExitButton)
        commentInput = view.findViewById(R.id.commentInput)

        loadValues()
        when (CurrentState.mode) {
            Mode.INVENTORY, Mode.FINAL_INVENTORY -> {
                itemNameInput = view.findViewById(R.id.itemNameInput)
                deviceNumberInput = view.findViewById(R.id.deviceNumberInput)


                modifyAddButton.setOnClickListener {
                    val inventoryItem = Inventory(
                        itemNameInput.text.toString(),
                        deviceNumberInput.text.toString().toIntOrNull() ?: 0
                    )
                    modifyButtonListener(exit = false, inventoryItem = inventoryItem)
                }
                modifyExitButton.setOnClickListener {
                    val inventoryItem = Inventory(
                        itemNameInput.text.toString(),
                        deviceNumberInput.text.toString().toIntOrNull() ?: 0
                    )
                    modifyButtonListener(exit = true, inventoryItem = inventoryItem)
                }
            }
            Mode.INSTALL -> {
                companyName = view.findViewById(R.id.companyNameText)
                firstItemI = view.findViewById(R.id.firstItem)
                secondItemI = view.findViewById(R.id.secondItem)
                thirdItemI = view.findViewById(R.id.thirdItem)
                fourthItemI = view.findViewById(R.id.fourthItem)
                fifthItemI = view.findViewById(R.id.fifthItem)
                sixthItemI = view.findViewById(R.id.sixthItem)
                seventhItemI = view.findViewById(R.id.seventhItem)
                eightItemI = view.findViewById(R.id.eightItem)
                ninethItemI = view.findViewById(R.id.ninethItem)
                pinGo = view.findViewById(R.id.pinGo)
                pinGo.setOnClickListener {
                    refreshCoords()
                }

                initSpinnersInstall()


                modifyAddButton.setOnClickListener {
                    val companyInstallItem = CompanyInstall(
                        CurrentState.companySite ?: "",
                        InstallFirstItemEnum.entries[firstItemI.selectedItemPosition],
                        InstallSecondItemEnum.entries[secondItemI.selectedItemPosition],
                        thirdItemI.isChecked,
                        fourthItemI.isChecked,
                        fifthItemI.isChecked,
                        sixthItemI.isChecked,
                        seventhItemI.isChecked,
                        eightItemI.isChecked,
                        ninethItemI.isChecked
                    )
                    modifyButtonListener(exit = false, companyInstallItem = companyInstallItem)
                }
                modifyExitButton.setOnClickListener {
                    val companyInstallItem = CompanyInstall(
                        CurrentState.companySite ?: "",
                        InstallFirstItemEnum.entries[firstItemI.selectedItemPosition],
                        InstallSecondItemEnum.entries[secondItemI.selectedItemPosition],
                        thirdItemI.isChecked,
                        fourthItemI.isChecked,
                        fifthItemI.isChecked,
                        sixthItemI.isChecked,
                        seventhItemI.isChecked,
                        eightItemI.isChecked,
                        ninethItemI.isChecked,
                    )
                    modifyButtonListener(exit = true, companyInstallItem = companyInstallItem)

                }
            }
            Mode.DEMOLITION -> {
                companyName = view.findViewById(R.id.companyNameText)
                firstItemD = view.findViewById(R.id.firstItem)
                secondItemD = view.findViewById(R.id.secondItem)
                thirdItemD = view.findViewById(R.id.thirdItem)
                pinGo = view.findViewById(R.id.pinGo)
                pinGo.setOnClickListener {
                    refreshCoords()
                }

                initSpinnersDemolition()

                modifyAddButton.setOnClickListener {
                    val companyDemolitionItem = CompanyDemolition(
                        CurrentState.companySite ?: "",
                        DemolitionFirstItemEnum.entries[firstItemD.selectedItemPosition],
                        DemolitionSecondItemEnum.entries[secondItemD.selectedItemPosition],
                        thirdItemD.isChecked
                    )
                    modifyButtonListener(exit = false, companyDemolitionItem = companyDemolitionItem)
                }
                modifyExitButton.setOnClickListener {
                    val companyDemolitionItem = CompanyDemolition(
                        CurrentState.companySite ?: "",
                        DemolitionFirstItemEnum.entries[firstItemD.selectedItemPosition],
                        DemolitionSecondItemEnum.entries[secondItemD.selectedItemPosition],
                        thirdItemD.isChecked
                    )
                    modifyButtonListener(exit = true, companyDemolitionItem = companyDemolitionItem)
                }
            }
            else -> return
        }

    }

    private fun modifyButtonListener(
        inventoryItem: Inventory? = null,
        companyInstallItem: CompanyInstall? = null,
        companyDemolitionItem: CompanyDemolition? = null,
        exit: Boolean = false
    ) {
        Functions.showLoadingScreen(requireActivity())
        if (inventoryItem != null)
            return modifyInventory(inventoryItem, exit)
        if (companyInstallItem != null)
            return modifyCompanyInstall(companyInstallItem, exit)
        if (companyDemolitionItem != null)
            return modifyCompanyDemolition(companyDemolitionItem, exit)
    }

    private fun onViewLoaded() {
        Functions.hideLoadingScreen()
    }

    private fun addComment(){
        val comment = commentInput.text.toString()
        if(comment.isEmpty())return
        else{
            val data = hashMapOf(
                "Comment" to comment,
                "Timestamp" to Timestamp.now().toDate()
            )
            Database.db.collection(modeName).document(CurrentState.companySiteID ?: "").collection("Comments").add(data)
                .addOnFailureListener { ex ->
                    showError(
                        requireActivity(),
                        "Sikertelen megjegyzés hozzáadás\nNézd a logot",
                        "Error at adding comment in StatusModify: $data, $ex"
                    )
                }
        }
    }
    private fun modifyCompanyDemolition(companyDemolitionItem: CompanyDemolition, exit: Boolean = false) {
        addComment()
        val docID = CurrentState.companySiteID ?: ""
        val data = hashMapOf<String, Any>(
            "CompanyName" to companyDemolitionItem.companyName,
            "1" to companyDemolitionItem.firstItem.name,
            "2" to companyDemolitionItem.secondItem.name,
            "3" to companyDemolitionItem.thirdItem,
            "LastModified" to Timestamp.now().toDate()
        )
        Database.db.collection("Company_Demolition").document(docID).update(data).addOnSuccessListener {
            if(exit)launchFragment(requireActivity(), DemolitionFragment())
            else launchFragment(requireActivity(), SNAddFragment())
            showInfoDialog(
                requireActivity(),
                "Módosítás",
                "Telephely sikeresen módosítva!",
                "Vissza",
                hideLoading = false
            )
            logToFile("Updated: companysitename: ${companyDemolitionItem.companyName} programname: ${CurrentState.programName} docid: $docID") }
    }

    private fun modifyCompanyInstall(companyInstallItem: CompanyInstall, exit: Boolean = false) {
        addComment()
        val docID = CurrentState.companySiteID ?: ""
        val data = hashMapOf<String, Any>(
            "CompanyName" to companyInstallItem.companyName,
            "1" to companyInstallItem.firstItem.name,
            "2" to companyInstallItem.secondItem.name,
            "3" to companyInstallItem.thirdItem,
            "4" to companyInstallItem.fourthItem,
            "5" to companyInstallItem.fifthItem,
            "6" to companyInstallItem.sixthItem,
            "7" to companyInstallItem.seventhItem,
            "8" to companyInstallItem.eightItem,
            "9" to companyInstallItem.ninethItem,
            "LastModified" to Timestamp.now().toDate()
        )
        Database.db.collection("Company_Install").document(docID).update(data).addOnSuccessListener {
            if(exit)launchFragment(requireActivity(), InstallFragment())
            else launchFragment(requireActivity(), SNAddFragment())
            showInfoDialog(
                requireActivity(),
                "Módosítás",
                "Telephely sikeresen módosítva!",
                "Vissza",
                hideLoading = false
            )
            logToFile("Updated: companysitename: ${companyInstallItem.companyName} programname: ${CurrentState.programName} docid: $docID")
        }
    }

    private fun modifyInventory(inventoryItem: Inventory, exit: Boolean = false) {

        if (deviceNumberInput.text.isEmpty()) {
            showError(requireActivity(), "Adj meg darabszámot!")
            return
        }
        if (itemNameInput.text.length < 3) {
            showError(requireActivity(), "A tárgynév legalább 3 hosszú kell legyen!")
            return
        }

        addComment()

        val docID = CurrentState.companySiteID ?: ""
        val data = hashMapOf<String, Any>(
            "Quantity" to inventoryItem.quantity,
            "ItemName" to inventoryItem.itemName,
            "LastModified" to Timestamp.now().toDate()
        )
        var final = false
        if(CurrentState.mode == Mode.FINAL_INVENTORY)
            final = true
        Database.db.collection(modeName).document(docID).update(data).addOnSuccessListener {
            if(exit)launchFragment(requireActivity(), InventoryFragment(), final)
            else launchFragment(requireActivity(), SNAddFragment(), final)
            showInfoDialog(
                requireActivity(),
                "Módosítás",
                "Tárgy sikeresen módosítva!",
                "Rendben",
                hideLoading = false
            )
            logToFile("Updated $modeName: itemname: ${inventoryItem.itemName} programname: ${CurrentState.programName} docid: $docID")
        }
    }

    private fun initSpinnersInstall(){
        val firstArrayList = ArrayList<String>()
        for(i in InstallFirstItemEnum.entries)
            firstArrayList.add(i.toString())
        val firstArrayAdapter:ArrayAdapter<String> = ArrayAdapter(requireActivity(), R.layout.spinner_item, firstArrayList)
        firstArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        firstItemI.adapter = firstArrayAdapter

        val secondArrayList = ArrayList<String>()
        for(i in InstallSecondItemEnum.entries)
            secondArrayList.add(i.toString())
        val secondArrayAdapter:ArrayAdapter<String> = ArrayAdapter(requireActivity(), R.layout.spinner_item, secondArrayList)
        secondArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        secondItemI.adapter = secondArrayAdapter

    }

    private fun initSpinnersDemolition(){
        val firstArrayList = ArrayList<String>()
        for(i in DemolitionFirstItemEnum.entries)
            firstArrayList.add(i.toString())
        val firstArrayAdapter:ArrayAdapter<String> = ArrayAdapter(requireActivity(), R.layout.spinner_item, firstArrayList)
        firstArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        firstItemD.adapter = firstArrayAdapter

        val secondArrayList = ArrayList<String>()
        for(i in DemolitionSecondItemEnum.entries)
            secondArrayList.add(i.toString())
        val secondArrayAdapter:ArrayAdapter<String> = ArrayAdapter(requireActivity(), R.layout.spinner_item, secondArrayList)
        secondArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        secondItemD.adapter = secondArrayAdapter
    }

    private fun refreshCoords() {
        val context = requireActivity()
        Functions.showLoadingScreen(context)
        getLocation(context,
            onSuccess = { location ->
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                var data = hashMapOf<String, Any>("coord" to geoPoint)
                var docID: String? = null
                Database.db.collection("Coordinates").whereEqualTo("ref", CurrentState.companySiteID).get().addOnSuccessListener{
                    if(it.documents.isNotEmpty())
                        docID = it.documents[0].id
                }.addOnFailureListener {
                    showError(context, "Sikertelen koordináta lekérés", it.toString())
                }
                if(docID != null)Database.db.collection("Coordinates").document(docID!!).update(data).addOnSuccessListener {
                    showInfoDialog(context, "Rögzítés", "Koordináták sikeresen rögzítve!")
                }.addOnFailureListener {
                    showError(context, "Sikertelen koordináta rögzítés", it.toString())
                }
                if(docID == null) {
                    val companyRef = Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
                    data = hashMapOf("coord" to geoPoint, "ref" to companyRef)

                    Database.db.collection("Coordinates").add(data).addOnSuccessListener {
                        showInfoDialog(context, "Rögzítés", "Koordináták sikeresen rögzítve!")
                    }.addOnFailureListener {
                        showError(context, "Sikertelen koordináta rögzítés", it.toString())
                    }
                }
            },
            onError = { errorMessage ->
                showError(context, "Sikertelen pozíció lekérés!", errorMessage)
            }
        )
    }

    fun onBackCalled(){
        showWarning(requireActivity(), "Nem mentettél paraszt!!\nBiztosan kiszeretnél lépni mentés nélkül?", OperationSelectorFragment())
    }
}