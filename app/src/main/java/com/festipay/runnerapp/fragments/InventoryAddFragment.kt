package com.festipay.runnerapp.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.FragmentTransaction
import com.festipay.runnerapp.R
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.DateFormatter.LocalDateTimeToTimestamp
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.showError
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.util.Date

class InventoryAddFragment : Fragment() {
    private lateinit var addExitButton: Button
    private lateinit var addButton: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_inventory_add, container, false)
        initViews(view)

        addExitButton.setOnClickListener {
            showLoadingScreen(requireActivity())
            val (item, comment) = getValues(view)

            if (item.targyNev.length > 2)
                Database.db.collection("leltar").whereEqualTo("TargyNev", item.targyNev, )
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
            else
                showError(requireActivity(), "A tárgynév legalább 3 hosszú kell legyen!")

        }

        addButton.setOnClickListener {
            showLoadingScreen(requireActivity())
            val (item, comment) = getValues(view)

            if (item.targyNev.length > 2)
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
            else
                showError(requireActivity(), "A tárgynév legalább 3 hosszú kell legyen!")
        }

        return view

    }

    private fun initViews(view: View) {
        addExitButton = view.findViewById(R.id.addExitButton)
        addButton = view.findViewById(R.id.addButton)
    }

    private fun getValues(view: View): Pair<Inventory, String> {
        val targyNev: String = view.findViewById<EditText>(R.id.targyNevInput).text.toString()
        val darabSzam: Int =
            view.findViewById<EditText>(R.id.darabSzamInput).text.toString().toIntOrNull() ?: 0
        val SN: Boolean =
            view.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.snSwitch).isChecked
        val comment: String = view.findViewById<EditText>(R.id.commentInput).text.toString()
        val item = Inventory(darabSzam, SN, targyNev, null, null, null)
        return Pair(item, comment)
    }

    private fun launchInventoryFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, InventoryFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun launchInventoryAddFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, InventoryAddFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun addData(item: Inventory, comment: String, exit: Boolean) {
        var data = hashMapOf(
            "Darabszam" to item.darabszam,
            "Program" to CurrentState.programName,
            "SN" to item.sn,
            "TargyNev" to item.targyNev
        )
        Database.db.collection("leltar").add(data).addOnSuccessListener { doc ->
            data = hashMapOf(
                "Comment" to comment,
                "Timestamp" to Timestamp.now().toDate()
            )
            Database.db.collection("leltar").document(doc.id).collection("Comments").add(data)
                .addOnSuccessListener {
                    if (exit) launchInventoryFragment()
                    else launchInventoryAddFragment()
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
        }.addOnFailureListener { ex ->
            showError(
                requireActivity(),
                "Sikertelen hozzáadás\nNézd a logot",
                "Error at adding item: $data, $ex"
            )
        }
    }
}