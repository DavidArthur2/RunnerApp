package com.festipay.runnerapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyDemolitionAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.DemolitionFirstItemEnum
import com.festipay.runnerapp.data.DemolitionSecondItemEnum
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter.TimestampToLocalDateTime
import com.festipay.runnerapp.utilities.DemolitionFilter
import com.festipay.runnerapp.utilities.Filter
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
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
        invokeFilter()
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
        val selectedItems = Filter.selectedDemolitionItems.copyOf()

        val builder = AlertDialog.Builder(requireActivity())

        val dialogLayout = layoutInflater.inflate(R.layout.custom_filter_dialog_layout, null)

        val block1TitleTextView = dialogLayout.findViewById<TextView>(R.id.block1TitleTextView)
        val block1CheckboxGroup = dialogLayout.findViewById<LinearLayout>(R.id.block1CheckboxGroup)
        val block2TitleTextView = dialogLayout.findViewById<TextView>(R.id.block2TitleTextView)
        val block2CheckboxGroup = dialogLayout.findViewById<LinearLayout>(R.id.block2CheckboxGroup)
        val block3TitleTextView = dialogLayout.findViewById<TextView>(R.id.block3TitleTextView)
        val block3CheckboxGroup = dialogLayout.findViewById<LinearLayout>(R.id.block3CheckboxGroup)


        block1TitleTextView.text = "Felderítés"
        block2TitleTextView.text = "Bontó"
        block3TitleTextView.text = "Elemek"

        for (i in filterOptions.indices) {
            val checkBox = CheckBox(requireActivity())
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            checkBox.text = filterOptions[i]
            checkBox.isChecked = selectedItems[i]
            val paddingInPixels = resources.getDimensionPixelSize(R.dimen.checkbox_padding)
            checkBox.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels)

            if (i in 0..2) {
                block1CheckboxGroup.addView(checkBox)
            } else if(i in 3 .. 6) {
                block2CheckboxGroup.addView(checkBox)
            }else
                block3CheckboxGroup.addView(checkBox)
        }

        builder.setView(dialogLayout)
            .setPositiveButton("Szűrés") { _, _ ->
                val updatedSelectedItems = mutableListOf<Boolean>()
                for (i in 0 until block1CheckboxGroup.childCount) {
                    val checkBox = block1CheckboxGroup.getChildAt(i) as CheckBox
                    updatedSelectedItems.add(checkBox.isChecked)
                }
                for (i in 0 until block2CheckboxGroup.childCount) {
                    val checkBox = block2CheckboxGroup.getChildAt(i) as CheckBox
                    updatedSelectedItems.add(checkBox.isChecked)
                }
                for (i in 0 until block3CheckboxGroup.childCount) {
                    val checkBox = block3CheckboxGroup.getChildAt(i) as CheckBox
                    updatedSelectedItems.add(checkBox.isChecked)
                }
                invokeFilter(updatedSelectedItems.toBooleanArray())
            }
            .setNegativeButton("Mégse", null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun invokeFilter(rawSelectedItems: BooleanArray? = null){
        val selectedItems: BooleanArray = rawSelectedItems ?: Filter.selectedDemolitionItems.copyOf()

        val selectedFilters:MutableList<DemolitionFilter> = mutableListOf()
        for (i in selectedItems.indices) {
            if (selectedItems[i]) {
                selectedFilters.add(DemolitionFilter.valueOfOrdinal(i))
            }
        }
        filter.filterList(option = selectedFilters)
        Filter.selectedDemolitionItems = selectedItems
    }

}