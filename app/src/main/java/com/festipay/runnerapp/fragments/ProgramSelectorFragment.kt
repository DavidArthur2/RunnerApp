package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.ProgramAdapter
import com.festipay.runnerapp.data.ProgramItem
import com.festipay.runnerapp.data.CurrentState
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProgramSelectorFragment : Fragment() {
private lateinit var programSelectorRecyclerView: RecyclerView
private lateinit var programItemList: ArrayList<ProgramItem>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_program_selector, container, false)
        initFragment()
        loadProgramList(view)
        return view
    }

    private fun initFragment(){
        val b = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        b.isVisible = false

        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = getString(R.string.program_selector_title)

        CurrentState.fragment = com.festipay.runnerapp.utilities.Fragment.PROGRAM
    }
    private fun loadProgramList(view: View){
        programItemList = arrayListOf<ProgramItem>()
        Database.db.collection("programok").get().addOnSuccessListener { result ->
            if(!result.isEmpty){
                for(doc in result){
                    programItemList.add(ProgramItem(doc.data["ProgramNev"] as String))
                }
                setupView(view)
            }
            else{
                showError(requireContext(), "Nincs felvéve egy program se")
            }
        }.addOnFailureListener { exception ->
            showError(requireContext(), "Can't read documents in programok: $exception")
        }
    }
    private fun setupView(view: View){

        programSelectorRecyclerView = view.findViewById(R.id.programSelectorRecyclerView)
        programSelectorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        programSelectorRecyclerView.setHasFixedSize(true)


        val adapt = ProgramAdapter(programItemList)
        programSelectorRecyclerView.adapter = adapt

        adapt.setOnItemClickListener(object : ProgramAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, programItem: ProgramItem) {
                CurrentState.programName = programItem.title

                requireActivity()
                    .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    .selectedItemId = R.id.install

            }
        })
    }
}

