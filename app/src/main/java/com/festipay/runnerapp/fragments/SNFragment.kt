package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CommentsAdapter
import com.festipay.runnerapp.adapters.SNAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Program
import com.festipay.runnerapp.data.SN
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class SNFragment : Fragment(), IFragment<SN> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<SN>
    private lateinit var modeName: String
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
    private fun initFragment(){
        CurrentState.operation = OperationType.SN_HANDLING
        modeName = Database.mapCollectionModeName()
        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)
        CurrentState.fragmentType = when (CurrentState.mode) {
            Mode.INSTALL -> FragmentType.INSTALL_COMPANY_SN
            Mode.DEMOLITION -> FragmentType.DEMOLITION_COMPANY_SN
            Mode.INVENTORY -> FragmentType.INVENTORY_ITEM_SN
            Mode.FINAL_INVENTORY -> FragmentType.FINAL_INVENTORY_ITEM_SN
            else -> FragmentType.INVENTORY_ITEM_SN

        }
        appBar.title = "${CurrentState.companySite} - DID kezelés"

    }

    private fun initViews(view: View) {
        view.findViewById<FloatingActionButton>(R.id.snFloatingActionButton)
            .setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
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
    override fun onViewLoaded(){
        Functions.hideLoadingScreen()
    }
    override fun loadList(view: View) {
        itemList = arrayListOf()
        Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
            .collection("SN").get().addOnSuccessListener { result ->
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
                    requireActivity(),
                    "Sikertelen SN beolvasás",
                    "companydocid: ${CurrentState.companySiteID} error: $it"
                )
            }
    }

    override fun loadComments(view: View) {
    }
    override fun setupView(view: View) {
        recyclerView = view.findViewById(R.id.snRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        val adapt = SNAdapter(itemList)
        recyclerView.adapter = adapt

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapt.setOnItemClickListener(object : SNAdapter.OnItemDeleteListener {
            override fun onItemDelete(position: Int, snItem: SN) {
                showLoadingScreen(requireActivity())
                Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
                    .collection("SN").document(snItem.docID).delete().addOnSuccessListener {
                        Functions.launchFragment(requireActivity(), SNFragment())
                        Functions.showInfoDialog(
                            requireActivity(),
                            "Törlés",
                            "Sikeresen törölted az SN-t!",
                            "Vissza",
                            false
                        )
                    }.addOnFailureListener {
                        showError(
                            requireActivity(),
                            "SN törlése sikertelen!",
                            "companydocid: ${CurrentState.companySiteID} sndocid: ${snItem.docID} error: $it"
                        )
                    }
            }
        })
    }

}