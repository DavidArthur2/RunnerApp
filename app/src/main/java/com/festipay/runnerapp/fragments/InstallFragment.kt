package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyInstallAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Comments
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.DateFormatter.TimestampToLocalDateTime
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class InstallFragment : Fragment(), IFragment<CompanyInstall> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<CompanyInstall>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_install, container, false)

        CurrentState.mode = Mode.INSTALL

        val bottomView = requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomView.isVisible = true

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.install_title)}"

        loadList(view)

        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INSTALL

        return view
    }
    override fun onViewLoaded(){
        hideLoadingScreen()
    }
    override fun loadList(view: View){
        itemList = arrayListOf<CompanyInstall>()
        Database.db.collection("telephely_telepites")
            .whereEqualTo("Program", CurrentState.programName)
            .orderBy("TelephelyNev", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
            if(!result.isEmpty){
                for(doc in result){
                    itemList.add(CompanyInstall(
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
                showError(requireContext(), "Nincs felvéve telephely!")
            }
        }.addOnFailureListener { exception ->
            showError(requireContext(), "Can't read documents in telephelyek: $exception")
        }
    }

    override fun loadComments(view: View){
        for(it in itemList) {
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
                }
                    if(itemList.last() == it)setupView(view)
            }
        }
    }

    override fun setupView(view: View){

        recyclerView = view.findViewById(R.id.companyInstallRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        val adapt = CompanyInstallAdapter(itemList)
        recyclerView.adapter = adapt

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


        adapt.setOnItemClickListener(object : CompanyInstallAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, companyInstall: CompanyInstall) {
                CurrentState.companySite = companyInstall.telephelyNev
                CurrentState.companySiteID = companyInstall.docID
                Functions.launchFragment(requireActivity(), OperationSelectorFragment())

            }
        })
    }


}