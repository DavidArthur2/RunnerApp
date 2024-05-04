package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.RViews.CompanyInstallAdapter
import com.festipay.runnerapp.RViews.ProgramAdapter
import com.festipay.runnerapp.RViews.ProgramItem
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.CurrentState
import com.festipay.runnerapp.data.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter.TimestampToLocalDateTime
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class InstallFragment : Fragment() {
    private lateinit var companySelectorRecyclerView: RecyclerView
    private lateinit var companyInstallList: ArrayList<CompanyInstall>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_install, container, false)

        CurrentState.mode = Mode.INSTALL

        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .isVisible = true

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)

        appBar.title = "${CurrentState.programName} - ${getString(R.string.install_title)}"

        loadCompanyList(view)

        return view
    }

    private fun loadCompanyList(view: View){
        companyInstallList = arrayListOf<CompanyInstall>()
        Database.db.collection("telephely_telepites")
            .whereEqualTo("Program", CurrentState.programName)
            .orderBy("TelephelyNev", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
            if(!result.isEmpty){
                for(doc in result){
                    companyInstallList.add(CompanyInstall(
                        doc.data["TelephelyNev"] as String,
                        doc.data["Kiadva"] as Boolean,
                        doc.data["NemKirakhato"] as Boolean,
                        doc.data["Kirakva"] as Boolean,
                        doc.data["Eloszto"] as Boolean,
                        doc.data["Aram"] as Boolean,
                        doc.data["Szoftver"] as Boolean,
                        doc.data["Param"] as Boolean,
                        doc.data["Teszt"] as Boolean,
                        null,
                        null,
                        null,
                        doc.id
                    ))
                }
                loadComments(view)

            }
            else{
                showError(requireContext(), "Nincs felvéve egy telephely se")
            }
        }.addOnFailureListener { exception ->
            showError(requireContext(), "Can't read documents in telephelyek: $exception")
        }
    }

    private fun loadComments(view: View){
        for(it in companyInstallList) {
            Database.db.collection("telephely_telepites").document(it.docID).collection("Comments").orderBy("Timestamp", Query.Direction.ASCENDING)
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
                    it.comments = Comments(comments)
                    if(it.comments!!.megjegyzesek.isNotEmpty())it.utolsoMegjegyzes = it.comments!!.megjegyzesek.last()
                    setupView(view)
                }
            }
        }
    }

    private fun setupView(view: View){

        companySelectorRecyclerView = view.findViewById(R.id.companyInstallRecyclerView)
        companySelectorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        companySelectorRecyclerView.setHasFixedSize(true)


        val adapt = CompanyInstallAdapter(companyInstallList)
        companySelectorRecyclerView.adapter = adapt

        adapt.setOnItemClickListener(object : CompanyInstallAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, companyInstall: CompanyInstall) {
                CurrentState.companySite = companyInstall.telephelyNev



            }
        })
    }

}