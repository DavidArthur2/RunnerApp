package com.festipay.runnerapp.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.isVisible
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp

class InventoryAddFragment : Fragment() {
    private lateinit var addExitButton: Button
    private lateinit var addButton: Button

    private lateinit var commentInput : EditText
    private lateinit var itemNameInput: EditText
    private lateinit var deviceNumberInput: EditText
    private var modeName: String = "Inventory"
    private var final: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_inventory_add, container, false)
        initViews(view)


        addExitButton.setOnClickListener {
            showLoadingScreen(requireActivity())
            addButtonListener(exit = true)
        }
        addButton.setOnClickListener {
            showLoadingScreen(requireActivity())
            addButtonListener(exit = false)
        }

        return view

    }

    private fun addButtonListener(exit: Boolean) {
        val (item, comment) = getValues()

        if (deviceNumberInput.text.isEmpty()) {
            showError(requireActivity(), "Adj meg darabszámot!")
            return
        }
        if (item.itemName.length < 3) {
            showError(requireActivity(), "A tárgynév legalább 3 hosszú kell legyen!")
            return
        }
        Database.db.collection("Inventory").whereEqualTo("TargyNev", item.itemName)
            .whereEqualTo("ProgramName", CurrentState.programName).get().addOnSuccessListener {
                if (it.isEmpty)
                    addData(item, comment, exit = exit)
                else
                    showError(
                        requireActivity(),
                        "Már létezik ilyen tárgynév ezen a programon!",
                        "itemname: ${item.itemName} programname: ${CurrentState.programName}"
                    )
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideLoadingScreen()
    }

    private fun initViews(view: View) {
        if(arguments?.getString("final") != null){
            final = true
            modeName = "Final_Inventory"
        }
        if(!final)CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INVENTORY_ITEM_ADD
        else CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.FINAL_INVENTORY_ITEM_ADD

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible = false
        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title =
            "${CurrentState.programName} - ${if(final)"Záró" else ""} ${getString(R.string.inventory_string)} - Hozzáadás"

        addExitButton = view.findViewById(R.id.modifyExitButton)
        addButton = view.findViewById(R.id.modifyButton)
        itemNameInput = view.findViewById(R.id.itemNameInput)
        deviceNumberInput = view.findViewById(R.id.deviceNumberInput)
        commentInput = view.findViewById(R.id.commentInput)

        itemNameInput.clearComposingText()
        deviceNumberInput.clearComposingText()
        commentInput.clearComposingText()
        commentInput.clearComposingText()
    }

    private fun getValues(): Pair<Inventory, String> {
        val itemName = itemNameInput.text.toString()
        val deviceNumber = deviceNumberInput.text.toString().toIntOrNull() ?: 0
        val comment = commentInput.text.toString()
        val item = Inventory(itemName, deviceNumber)
        return Pair(item, comment)
    }

    private fun addData(item: Inventory, comment: String, exit: Boolean) {
        var data = hashMapOf(
            "Quantity" to item.quantity,
            "ProgramName" to CurrentState.programName,
            "ItemName" to item.itemName
        )
        Database.db.collection(modeName).add(data).addOnSuccessListener { doc ->

            if (comment.isEmpty()) {
                if (exit) launchFragment(requireActivity(), InventoryFragment(), final)
                else launchFragment(requireActivity(), InventoryAddFragment(), final)
                showInfoDialog(
                    requireActivity(),
                    "Hozzáadás",
                    "\'${item.itemName}\' sikeresen hozzáadva!",
                    "Rendben",
                    false
                )
            } else {
                data = hashMapOf(
                    "Comment" to comment,
                    "Timestamp" to Timestamp.now().toDate()
                )
                Database.db.collection(modeName).document(doc.id).collection("Comments").add(data)
                    .addOnSuccessListener {
                        if (exit) launchFragment(requireActivity(), InventoryFragment(), final)
                        else launchFragment(requireActivity(), InventoryAddFragment(), final)
                        showInfoDialog(
                            requireActivity(),
                            "Hozzáadás",
                            "${item.itemName} sikeresen hozzáadva!",
                            "Rendben",
                            false
                        )
                    }.addOnFailureListener { ex ->
                        showError(
                            requireActivity(),
                            "Sikertelen hozzáadás\nNézd a logot",
                            "Error at adding comment: $data, $ex"
                        )
                    }
            }
        }.addOnFailureListener { ex ->
            showError(
                requireActivity(),
                "Sikertelen hozzáadás\nNézd a logot",
                "Error at adding item: $data, $ex"
            )
        }
    }
}