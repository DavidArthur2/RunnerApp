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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.InventoryAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.data.References.Companion.comments_ref
import com.festipay.runnerapp.data.References.Companion.mode_ref
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.DateFormatter.timestampToLocalDateTime
import com.festipay.runnerapp.utilities.Filter
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
    private lateinit var filter: Filter<Inventory>
    private var final: Boolean = false
    private var modeName: String = "Inventory"
    private lateinit var context: FragmentActivity
    
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

            context.supportFragmentManager.beginTransaction()
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
        context = requireActivity()
        if(arguments?.getString("final") != null) {
            final = true
            modeName = "Final_Inventory"
        }

        if(!final)CurrentState.mode = Mode.INVENTORY
        else CurrentState.mode = Mode.FINAL_INVENTORY

        val appBar: Toolbar = context.findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${if(final)"Záró" else ""} ${getString(R.string.inventory_string)}"
        setHasOptionsMenu(true)

        context.findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible =
            true
        if(!final)CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INVENTORY
        else CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.FINAL_INVENTORY
    }

    override fun onViewLoaded() {
        filter = Filter(adapter, itemList)
        hideLoadingScreen()
    }

    override fun loadList(view: View) {
        itemList = arrayListOf<Inventory>()
        mode_ref
            .whereEqualTo("ProgramName", CurrentState.programName)
            .orderBy("ItemName", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                try {
                    if (!result.isEmpty) {
                        for (doc in result) {
                            itemList.add(
                                Inventory(
                                    itemName = doc.data["ItemName"] as String,
                                    quantity = (doc.data["Quantity"] as Long).toInt(),
                                    docID = doc.id,
                                    lastModified = timestampToLocalDateTime(doc.data["LastModified"] as Timestamp?),
                                    lastComment = null
                                )
                            )
                        }
                        loadComments(view)

                    } else {
                        showError(context, "Nincs felvéve tárgy!")
                    }
                } catch (ex: Exception) {
                    showError(context, "Error in $modeName loadInventoryList: $ex")
                }

            }.addOnFailureListener { exception ->
                showError(context, "Can't read documents in $modeName inventoryitems: $exception")
            }
    }

    override fun loadComments(view: View) {
        for (it in itemList) {
            comments_ref(it.docID)
                .orderBy("Timestamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val comments: MutableList<Comment> = mutableListOf()
                        for (doc in result) {
                            comments.add(
                                Comment(
                                    megjegyzes = doc.data["Comment"] as String,
                                    megjegyzesIdo = timestampToLocalDateTime(doc.data["Timestamp"] as Timestamp)
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
        recyclerView.layoutManager = LinearLayoutManager(context)
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
                launchFragment(context, OperationSelectorFragment())

            }
        })
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
                filter.filterList(text = newText ?: "")
                return true
            }
        })
    }

}