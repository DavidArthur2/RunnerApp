package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.SearchView
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
    private lateinit var adapter: InventoryAdapter
    private var final: Boolean = false
    private var modeName: String = "Inventory"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory, container, false)

        initFragment()
        initViews(view)
        loadList(view)
        return view
    }

    private fun initViews(view: View) {
        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            val frag = InventoryAddFragment()
            val args = Bundle()
            args.putString("final", "final")
            if(final)frag.arguments = args

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.frameLayout, frag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }

    private fun initFragment() {
        if(arguments?.getString("final") != null) {
            final = true
            modeName = "Final_Inventory"
        }

        if(!final)CurrentState.mode = Mode.INVENTORY
        else CurrentState.mode = Mode.FINAL_INVENTORY

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${if(final)"Záró" else ""} ${getString(R.string.inventory_string)}"
        setHasOptionsMenu(true)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible =
            true
        if(!final)CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INVENTORY
        else CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.FINAL_INVENTORY
    }

    override fun onViewLoaded() {
        hideLoadingScreen()
    }

    override fun loadList(view: View) {
        itemList = arrayListOf<Inventory>()
        Database.db.collection(modeName)
            .whereEqualTo("ProgramName", CurrentState.programName)
            .orderBy("ItemName", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                try {
                    if (!result.isEmpty) {
                        for (doc in result) {
                            itemList.add(
                                Inventory(
                                    doc.data["ItemName"] as String,
                                    (doc.data["Quantity"] as Long).toInt(),
                                    doc.id,
                                    null
                                )
                            )
                        }
                        loadComments(view)

                    } else {
                        showError(requireContext(), "Nincs felvéve tárgy!")
                    }
                } catch (ex: Exception) {
                    showError(requireContext(), "Error in $modeName loadInventoryList: $ex")
                }

            }.addOnFailureListener { exception ->
                showError(requireContext(), "Can't read documents in $modeName inventoryitems: $exception")
            }
    }

    override fun loadComments(view: View) {
        for (it in itemList) {
            Database.db.collection(modeName).document(it.docID).collection("Comments")
                .orderBy("Timestamp", Query.Direction.ASCENDING)
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
                        it.lastComment = comments.last()
                    }
                    if (itemList.last() == it)
                        setupView(view)
                }
        }
    }

    override fun setupView(view: View) {

        recyclerView = view.findViewById(R.id.inventoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        adapter = InventoryAdapter(itemList)
        recyclerView.adapter = adapter

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapter.setOnItemClickListener(object : InventoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, inventoryItem: Inventory) {
                CurrentState.companySite = inventoryItem.itemName
                CurrentState.companySiteID = inventoryItem.docID
                launchFragment(requireActivity(), OperationSelectorFragment())

            }
        })
    }

    private fun filter(text: String) {
        val filteredList = itemList.filter {
            it.itemName.lowercase().contains(text.lowercase())
        }
        adapter.filterList(filteredList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText ?: "")
                return true
            }
        })
    }

}