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
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.InstallFirstItemEnum
import com.festipay.runnerapp.data.InstallSecondItemEnum
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

        initFragment()
        loadList(view)

        return view
    }

    fun initFragment(){
        CurrentState.mode = Mode.INSTALL
        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INSTALL

        val bottomView = requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomView.isVisible = true

        val appBar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.install_title)}"
    }
    override fun onViewLoaded(){
        hideLoadingScreen()
    }
    override fun loadList(view: View){
        itemList = arrayListOf()
        Database.db.collection("telephely_telepites")
            .whereEqualTo("ProgramName", CurrentState.programName)
            .orderBy("CompanyName", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
            if(!result.isEmpty){
                for(doc in result){
                    itemList.add(CompanyInstall(
                        doc.data["CompanyName"] as String,
                        InstallFirstItemEnum.valueOf(doc.data["1"] as String),
                        InstallSecondItemEnum.valueOf(doc.data["2"] as String),
                        doc.data["3"] as Boolean,
                        doc.data["4"] as Boolean,
                        doc.data["5"] as Boolean,
                        doc.data["6"] as Boolean,
                        doc.data["7"] as Boolean,
                        doc.data["8"] as Boolean,
                        doc.data["9"] as Boolean,
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
                    it.lastComment = comments.first()
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
                CurrentState.companySite = companyInstall.companyName
                CurrentState.companySiteID = companyInstall.docID
                Functions.launchFragment(requireActivity(), OperationSelectorFragment())

            }
        })
    }


}