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
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.showError
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

        CurrentState.mode = Mode.DEMOLITION

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.demolition_string)}"

        loadList(view)

        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.DEMOLITION

        return view
    }

    override fun loadList(view: View){
        itemList = arrayListOf<CompanyDemolition>()
        Database.db.collection("telephely_bontas")
            .whereEqualTo("Program", CurrentState.programName)
            .orderBy("TelephelyNev", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                if(!result.isEmpty){
                    for(doc in result){
                        itemList.add(
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
                        it.comments = Comments(comments)
                        if(it.comments!!.megjegyzesek.isNotEmpty())it.utolsoMegjegyzes = it.comments!!.megjegyzesek.last()
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

        adapt.setOnItemClickListener(object : CompanyDemolitionAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, companyDemolition: CompanyDemolition) {
                CurrentState.companySite = companyDemolition.telephelyNev



            }
        })
    }
}