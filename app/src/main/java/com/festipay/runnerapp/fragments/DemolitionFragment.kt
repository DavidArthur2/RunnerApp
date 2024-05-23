package com.festipay.runnerapp.fragments

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyDemolitionAdapter
import com.festipay.runnerapp.adapters.InventoryAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.DemolitionFirstItemEnum
import com.festipay.runnerapp.data.DemolitionSecondItemEnum
import com.festipay.runnerapp.data.InstallFirstItemEnum
import com.festipay.runnerapp.data.InstallSecondItemEnum
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.DateFormatter.TimestampToLocalDateTime
import com.festipay.runnerapp.utilities.DemolitionFilter
import com.festipay.runnerapp.utilities.Filter
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.InstallFilter
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class DemolitionFragment : Fragment(), IFragment<CompanyDemolition> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<CompanyDemolition>
    private lateinit var adapter: CompanyDemolitionAdapter
    private lateinit var filter: Filter<CompanyDemolition>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_demolition, container, false)

        initFragment()
        loadList(view)
        return view
    }
    private fun initFragment(){
        CurrentState.mode = Mode.DEMOLITION
        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.DEMOLITION

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible = true

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.demolition_string)}"
        setHasOptionsMenu(true)
    }
    override fun onViewLoaded(){
        filter = Filter(adapter, itemList)
        hideLoadingScreen()
    }
    override fun loadList(view: View){
        itemList = arrayListOf<CompanyDemolition>()
        Database.db.collection("Company_Demolition")
            .whereEqualTo("ProgramName", CurrentState.programName)
            .orderBy("CompanyName", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                if(!result.isEmpty){
                    for(doc in result){
                        itemList.add(
                            CompanyDemolition(
                                doc.data["CompanyName"] as String,
                                DemolitionFirstItemEnum.valueOf(doc.data["1"] as String),
                                DemolitionSecondItemEnum.valueOf(doc.data["2"] as String),
                                doc.data["3"] as Boolean,
                                doc.id,
                                lastModified = TimestampToLocalDateTime(doc.data["LastModified"] as Timestamp?)
                        )
                        )
                    }
                    loadComments(view)

                }
                else{
                    showError(requireContext(), "Nincs felvéve telephely!")
                }
            }.addOnFailureListener { exception ->
                showError(requireContext(), "Can't read documents in telephelyek_bontas: $exception")
            }
    }

    override fun loadComments(view: View){
        for(it in itemList) {
            Database.db.collection("Company_Demolition").document(it.docID).collection("Comments").orderBy("Timestamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val comments: MutableList<Comment> = mutableListOf()
                        for (doc in result) {
                            comments.add(
                                Comment(
                                    doc.data["Comment"] as String,
                                    TimestampToLocalDateTime(doc.data["Timestamp"] as Timestamp)
                                )
                            )
                        }
                        it.lastComment = comments.last()
                    }
                    if(itemList.last() == it)setupView(view)
                }
        }
    }

    override fun setupView(view: View){

        recyclerView = view.findViewById(R.id.companyDemolitionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        adapter = CompanyDemolitionAdapter(itemList)
        recyclerView.adapter = adapter

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapter.setOnItemClickListener(object : CompanyDemolitionAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, companyDemolition: CompanyDemolition) {
                CurrentState.companySite = companyDemolition.companyName
                CurrentState.companySiteID = companyDemolition.docID
                Functions.launchFragment(requireActivity(), OperationSelectorFragment())


            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        val filterItem = menu.findItem(R.id.actionFilter)

        filterItem.setOnMenuItemClickListener {
            showFilterDialog()
            true
        }
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

    private fun showFilterDialog() {
        val filterOptions = DemolitionFilter.toCharSequence()
        val selectedItems = filter.selectedInstallItems.copyOf()

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Válassz egy szűrőt")
            .setMultiChoiceItems(filterOptions, selectedItems) { _, which, isChecked ->
                selectedItems[which] = isChecked
            }
            .setPositiveButton("Szűrés") { _, _ ->
                val selectedFilters:MutableList<DemolitionFilter> = mutableListOf()
                for (i in selectedItems.indices) {
                    if (selectedItems[i]) {
                        selectedFilters.add(DemolitionFilter.valueOfOrdinal(i))
                    }
                }
                filter.filterList(option = selectedFilters)
                filter.selectedDemolitionItems = selectedItems
            }
            .setNegativeButton("Mégse", null)

        val dialog = builder.create()
        dialog.show()
    }


}