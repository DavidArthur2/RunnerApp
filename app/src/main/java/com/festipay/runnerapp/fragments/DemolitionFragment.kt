package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyDemolitionAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CurrentState
import com.festipay.runnerapp.data.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.showError
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class DemolitionFragment : Fragment() {
    private lateinit var companySelectorRecyclerView: RecyclerView
    private lateinit var companyDemolitionList: ArrayList<CompanyDemolition>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_demolition, container, false)

        CurrentState.mode = Mode.DEMOLITION

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.demolition_string)}"

        loadCompanyList(view)

        CurrentState.fragment = com.festipay.runnerapp.utilities.Fragment.DEMOLITION

        return view
    }

    private fun loadCompanyList(view: View){
        companyDemolitionList = arrayListOf<CompanyDemolition>()
        Database.db.collection("telephely_bontas")
            .whereEqualTo("Program", CurrentState.programName)
            .orderBy("TelephelyNev", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                if(!result.isEmpty){
                    for(doc in result){
                        companyDemolitionList.add(
                            CompanyDemolition(
                                doc.data["TelephelyNev"] as String,
                                (doc.data["Eszkozszam"] as Long).toInt(),
                            doc.data["Folyamatban"] as Boolean,
                            doc.data["Csomagolt"] as Boolean,
                            doc.data["Autoban"] as Boolean,
                            doc.data["BazisLeszereles"] as Boolean,
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
                    showError(requireContext(), "Nincs felvéve egy telephely se")
                }
            }.addOnFailureListener { exception ->
                showError(requireContext(), "Can't read documents in telephelyek_bontas: $exception")
            }
    }

    private fun loadComments(view: View){
        for(it in companyDemolitionList) {
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
                        it.comments = Comments(comments)
                        if(it.comments!!.megjegyzesek.isNotEmpty())it.utolsoMegjegyzes = it.comments!!.megjegyzesek.last()
                    }
                    if(companyDemolitionList.last() == it)setupView(view)
                }
        }
    }

    private fun setupView(view: View){

        companySelectorRecyclerView = view.findViewById(R.id.companyDemolitionRecyclerView)
        companySelectorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        companySelectorRecyclerView.setHasFixedSize(true)


        val adapt = CompanyDemolitionAdapter(companyDemolitionList)
        companySelectorRecyclerView.adapter = adapt

        adapt.setOnItemClickListener(object : CompanyDemolitionAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, companyDemolition: CompanyDemolition) {
                CurrentState.companySite = companyDemolition.telephelyNev



            }
        })
    }
}