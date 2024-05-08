package com.festipay.runnerapp.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp

class InventoryAddFragment : Fragment() {
    private lateinit var addExitButton: Button
    private lateinit var addButton: Button
    private lateinit var targyNevInput: EditText
    private lateinit var darabSzamInput: EditText
    private lateinit var snSwitch: com.google.android.material.switchmaterial.SwitchMaterial
    private lateinit var commentInput: EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_inventory_add, container, false)
        initViews(view)

        addExitButton.setOnClickListener {
            showLoadingScreen(requireActivity())
            val (item, comment) = getValues()

            if (darabSzamInput.text.isEmpty()) {
                showError(requireActivity(), "Adj meg darabszámot!")
                return@setOnClickListener
            }
            if (item.targyNev.length < 3) {
                showError(requireActivity(), "A tárgynév legalább 3 hosszú kell legyen!")
                return@setOnClickListener
            }
            Database.db.collection("leltar").whereEqualTo("TargyNev", item.targyNev)
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

        addButton.setOnClickListener {
            showLoadingScreen(requireActivity())
            val (item, comment) = getValues()

            if (darabSzamInput.text.isEmpty()) {
                showError(requireActivity(), "Adj meg darabszámot!")
                return@setOnClickListener
            }
            if (item.targyNev.length < 3) {
                showError(requireActivity(), "A tárgynév legalább 3 karakter hosszú kell legyen!")
                return@setOnClickListener
            }
            Database.db.collection("leltar").whereEqualTo("TargyNev", item.targyNev)
                .whereEqualTo("Program", CurrentState.programName).get().addOnSuccessListener {
                    if (it.isEmpty)
                        addData(item, comment, exit = false)
                    else
                        showError(
                            requireActivity(),
                            "Már létezik ilyen tárgynév ezen a programon!",
                            "itemname: ${item.targyNev} programname: ${CurrentState.programName}"
                        )
                }
        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideLoadingScreen()
    }

    private fun initViews(view: View) {
        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INVENTORY_ITEM_ADD

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible = false
        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title =
            "${CurrentState.programName} - ${getString(R.string.inventory_string)} - Hozzáadás"

        addExitButton = view.findViewById(R.id.addExitButton)
        addButton = view.findViewById(R.id.addButton)
        targyNevInput = view.findViewById(R.id.targyNevInput)
        darabSzamInput = view.findViewById(R.id.darabSzamInput)
        snSwitch = view.findViewById(R.id.snSwitch)
        commentInput = view.findViewById(R.id.commentInput)

        targyNevInput.clearComposingText()
        darabSzamInput.clearComposingText()
        snSwitch.clearComposingText()
        commentInput.clearComposingText()
    }

    private fun getValues(): Pair<Inventory, String> {
        val targyNev = targyNevInput.text.toString()
        val darabSzam = darabSzamInput.text.toString().toIntOrNull() ?: 0
        val SN = snSwitch.isChecked
        val comment = commentInput.text.toString()
        val item = Inventory(darabSzam, SN, targyNev, null, null, null)
        return Pair(item, comment)
    }

    private fun addData(item: Inventory, comment: String, exit: Boolean) {
        var data = hashMapOf(
            "Darabszam" to item.darabszam,
            "Program" to CurrentState.programName,
            "SN" to item.sn,
            "TargyNev" to item.targyNev
        )
        Database.db.collection("leltar").add(data).addOnSuccessListener { doc ->

            if (comment.isEmpty()) {
                if (exit) launchFragment(requireActivity(), InventoryFragment())
                else launchFragment(requireActivity(), InventoryAddFragment())
                showInfoDialog(
                    requireActivity(),
                    "Hozzáadás",
                    "${item.targyNev} sikeresen hozzáadva!",
                    "Rendben",
                    false
                )
            } else {
                data = hashMapOf(
                    "Comment" to comment,
                    "Timestamp" to Timestamp.now().toDate()
                )
                Database.db.collection("leltar").document(doc.id).collection("Comments").add(data)
                    .addOnSuccessListener {
                        if (exit) launchFragment(requireActivity(), InventoryFragment())
                        else launchFragment(requireActivity(), InventoryAddFragment())
                        showInfoDialog(
                            requireActivity(),
                            "Hozzáadás",
                            "${item.targyNev} sikeresen hozzáadva!",
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