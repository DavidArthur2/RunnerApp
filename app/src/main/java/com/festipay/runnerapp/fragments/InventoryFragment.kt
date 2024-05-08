package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.InventoryAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class InventoryFragment : Fragment(), IFragment<Inventory> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<Inventory>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory, container, false)

        CurrentState.mode = Mode.INVENTORY

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.inventory_string)}"

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible = true

        loadList(view)

        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INVENTORY

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.frameLayout, InventoryAddFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }

        return view
    }
    override fun onViewLoaded(){
        hideLoadingScreen()
    }
    override fun loadList(view: View){
        itemList = arrayListOf<Inventory>()
        Database.db.collection("leltar")
            .whereEqualTo("Program", CurrentState.programName)
            .orderBy("TargyNev", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                try{
                    if(!result.isEmpty){
                        for(doc in result){
                            itemList.add(
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
                        showError(requireContext(), "Nincs felvéve tárgy!")
                    }
                }catch(ex: Exception){
                    showError(requireContext(), "Error in loadInventoryList: $ex")
                }

            }.addOnFailureListener { exception ->
                showError(requireContext(), "Can't read documents in leltáritems: $exception")
            }
    }

    override fun loadComments(view: View){
        for(it in itemList) {
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
                    if(itemList.last() == it)
                        setupView(view)
                }
        }
    }

    override fun setupView(view: View){

        recyclerView = view.findViewById(R.id.inventoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        val adapt = InventoryAdapter(itemList)
        recyclerView.adapter = adapt

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapt.setOnItemClickListener(object : InventoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, inventoryItem: Inventory) {
                CurrentState.companySite = inventoryItem.targyNev
                CurrentState.companySiteID = inventoryItem.docID
                launchFragment(requireActivity(), OperationSelectorFragment())

            }
        })
    }

}