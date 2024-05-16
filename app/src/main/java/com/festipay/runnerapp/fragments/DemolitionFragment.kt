package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyDemolitionAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.DemolitionFirstItemEnum
import com.festipay.runnerapp.data.DemolitionSecondItemEnum
import com.festipay.runnerapp.data.InstallFirstItemEnum
import com.festipay.runnerapp.data.InstallSecondItemEnum
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class DemolitionFragment : Fragment(), IFragment<CompanyDemolition> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<CompanyDemolition>
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
    }
    override fun onViewLoaded(){
        hideLoadingScreen()
    }
    override fun loadList(view: View){
        itemList = arrayListOf<CompanyDemolition>()
        Database.db.collection("telephely_bontas")
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
                                doc.id
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
            Database.db.collection("telephely_bontas").document(it.docID).collection("Comments").orderBy("Timestamp", Query.Direction.ASCENDING)
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
                        it.lastComment = comments.first()
                    }
                    if(itemList.last() == it)setupView(view)
                }
        }
    }

    override fun setupView(view: View){

        recyclerView = view.findViewById(R.id.companyDemolitionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        val adapt = CompanyDemolitionAdapter(itemList)
        recyclerView.adapter = adapt

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapt.setOnItemClickListener(object : CompanyDemolitionAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, companyDemolition: CompanyDemolition) {
                CurrentState.companySite = companyDemolition.companyName
                CurrentState.companySiteID = companyDemolition.docID
                Functions.launchFragment(requireActivity(), OperationSelectorFragment())


            }
        })
    }
}