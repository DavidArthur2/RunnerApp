package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyInstallAdapter
import com.festipay.runnerapp.adapters.InventoryAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.CurrentState
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.data.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class InventoryFragment : Fragment() {
    private lateinit var inventorySelectorRecyclerView: RecyclerView
    private lateinit var inventoryList: ArrayList<Inventory>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory, container, false)

        CurrentState.mode = Mode.INVENTORY

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.inventory_string)}"

        loadInventoryList(view)

        CurrentState.fragment = com.festipay.runnerapp.utilities.Fragment.INVENTORY

        return view
    }

    private fun loadInventoryList(view: View){
        inventoryList = arrayListOf<Inventory>()
        Database.db.collection("leltar")
            .whereEqualTo("Program", CurrentState.programName)
            .orderBy("TargyNev", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                try{
                    if(!result.isEmpty){
                        for(doc in result){
                            inventoryList.add(
                                Inventory(
                                    (doc.data["Darabszam"] as Long).toInt(),
                                    doc.data["SN"] as Boolean,
                                    doc.data["TargyNev"] as String,
                                    null,
                                    null,
                                    null,
                                    doc.id
                                )
                            )
                        }
                        loadComments(view)

                    }
                    else{
                        showError(requireContext(), "Nincs felvéve egy tárgy se")
                    }
                }catch(ex: Exception){
                    showError(requireContext(), "Error in loadInventoryList: $ex")
                }

            }.addOnFailureListener { exception ->
                showError(requireContext(), "Can't read documents in leltáritems: $exception")
            }
    }

    private fun loadComments(view: View){
        for(it in inventoryList) {
            Database.db.collection("leltar").document(it.docID).collection("Comments").orderBy("Timestamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val comments: MutableList<Comment> = mutableListOf()
                        for (doc in result) {
                            comments.add(
                                Comment(
                                    doc.data["Comment"] as String,
                                    DateFormatter.TimestampToLocalDateTime(doc.data["Timestamp"] as Timestamp)
                                )
                            )
                        }
                        it.comments = Comments(comments)
                        if(it.comments!!.megjegyzesek.isNotEmpty())it.utolsoMegjegyzes = it.comments!!.megjegyzesek.last()
                    }
                    if(inventoryList.last() == it)
                        setupView(view)
                }
        }
    }

    private fun setupView(view: View){

        inventorySelectorRecyclerView = view.findViewById(R.id.inventoryRecyclerView)
        inventorySelectorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        inventorySelectorRecyclerView.setHasFixedSize(true)


        val adapt = InventoryAdapter(inventoryList)
        inventorySelectorRecyclerView.adapter = adapt

        adapt.setOnItemClickListener(object : InventoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, inventoryItem: Inventory) {
                CurrentState.companySite = inventoryItem.targyNev




            }
        })
    }

}