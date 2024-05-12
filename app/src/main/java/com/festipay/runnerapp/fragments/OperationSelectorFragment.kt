package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyInstallAdapter
import com.festipay.runnerapp.adapters.OperationAdapter
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.google.android.material.bottomnavigation.BottomNavigationView

class OperationSelectorFragment(
) : Fragment(), IFragment<String> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_operation_selector, container, false)

        initFragment()
        loadList(view)
        onViewLoaded()
        return view
    }

    private fun initFragment(){
        val mode: String = when(CurrentState.mode){
            Mode.INVENTORY -> {
                CurrentState.fragmentType = FragmentType.INVENTORY_ITEM
                getString(R.string.inventory_string)
            }
            Mode.INSTALL -> {
                CurrentState.fragmentType = FragmentType.INSTALL_COMPANY
                getString(R.string.install_string)
            }
            Mode.DEMOLITION -> {
                CurrentState.fragmentType = FragmentType.DEMOLITION_COMPANY
                getString(R.string.demolition_string)
            }
            else -> getString(R.string.inventory_string)
        }


        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title =
            "${CurrentState.programName} - $mode - ${CurrentState.companySite}"

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible = false
    }

    override fun loadList(view: View) {
        itemList = arrayListOf()
        itemList.add(getString(R.string.status_modify))
        itemList.add(getString(R.string.comments))
        if(CurrentState.mode == Mode.DEMOLITION || CurrentState.mode == Mode.INSTALL ){
            itemList.add(getString(R.string.did))
            itemList.add(getString(R.string.gps))
        }
        if(CurrentState.mode == Mode.INVENTORY) itemList.add(getString(R.string.sn))
        setupView(view)
    }

    override fun loadComments(view: View) {

    }

    override fun setupView(view: View) {
        recyclerView = view.findViewById(R.id.operationSelectorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        val adapt = OperationAdapter(itemList)
        recyclerView.adapter = adapt

        adapt.setOnItemClickListener(object : OperationAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, operation: String) {
                when(operation){
                    getString(R.string.status_modify) ->{
                        Functions.launchFragment(requireActivity(), StatusModifyFragment())
                    }
                    getString(R.string.comments) ->{
                        Functions.launchFragment(requireActivity(), CommentsFragment())
                    }
                    getString(R.string.did),getString(R.string.sn) ->{
                        CurrentState.operation = OperationType.SN_HANDLING
                        Functions.launchFragment(requireActivity(), SNFragment())
                    }
                    getString(R.string.gps) ->{
                        CurrentState.operation = OperationType.GPS
                        //Functions.launchFragment(requireActivity(), GPSFragment())
                    }
                }


            }
        })
    }

    override fun onViewLoaded() {
        Functions.hideLoadingScreen()
    }
}