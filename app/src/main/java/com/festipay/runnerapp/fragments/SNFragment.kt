package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.SNAdapter
import com.festipay.runnerapp.data.References.Companion.sn_ref
import com.festipay.runnerapp.data.SN
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Filter
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SNFragment : Fragment(), IFragment<SN> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<SN>
    private lateinit var adapter: SNAdapter
    private lateinit var modeName: String
    private lateinit var filter: Filter<SN>
    private lateinit var context: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_s_n, container, false)
        initFragment()
        initViews(view)
        loadList(view)
        return view
    }

    private fun initFragment() {
        context = requireActivity()
        CurrentState.operation = OperationType.SN_HANDLING
        modeName = Database.mapCollectionModeName()
        val appBar: androidx.appcompat.widget.Toolbar = context.findViewById(R.id.toolbar)
        CurrentState.fragmentType = when (CurrentState.mode) {
            Mode.INSTALL -> FragmentType.INSTALL_COMPANY_SN
            Mode.DEMOLITION -> FragmentType.DEMOLITION_COMPANY_SN
            Mode.INVENTORY -> FragmentType.INVENTORY_ITEM_SN
            Mode.FINAL_INVENTORY -> FragmentType.FINAL_INVENTORY_ITEM_SN
            else -> FragmentType.INVENTORY_ITEM_SN

        }
        appBar.title = "${CurrentState.companySite} - DID kezelés"
        setHasOptionsMenu(true)

    }

    private fun initViews(view: View) {
        view.findViewById<FloatingActionButton>(R.id.snFloatingActionButton)
            .setOnClickListener {
                context.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.frameLayout, SNAddFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
    }

    override fun onViewLoaded() {
        filter = Filter(adapter, itemList)
        Functions.hideLoadingScreen()
    }

    override fun loadList(view: View) {
        itemList = arrayListOf()
        sn_ref().get().addOnSuccessListener { result ->
            if (!result.isEmpty) {
                for (doc in result) {
                    itemList.add(
                        SN(
                            doc.data["SN"] as String,
                            doc.id
                        )
                    )
                }
            }
            setupView(view)
        }.addOnFailureListener {
            showError(
                context,
                "Sikertelen SN beolvasás",
                "companydocid: ${CurrentState.companySiteID} error: $it"
            )
        }
    }

    override fun loadComments(view: View) {
    }

    override fun setupView(view: View) {
        recyclerView = view.findViewById(R.id.snRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)


        adapter = SNAdapter(itemList)
        recyclerView.adapter = adapter

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapter.setOnItemClickListener(object : SNAdapter.OnItemDeleteListener {
            override fun onItemDelete(position: Int, snItem: SN) {
                showLoadingScreen(context)
                sn_ref(snItem.docID).delete().addOnSuccessListener {
                    Functions.launchFragment(context, SNFragment())
                    Functions.showInfoDialog(
                        context,
                        "Törlés",
                        "Sikeresen törölted az SN-t!",
                        "Vissza",
                        false
                    )
                }.addOnFailureListener {
                    showError(
                        context,
                        "SN törlése sikertelen!",
                        "companydocid: ${CurrentState.companySiteID} sndocid: ${snItem.docID} error: $it"
                    )
                }
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