package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.ProgramAdapter
import com.festipay.runnerapp.data.Program
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProgramSelectorFragment : Fragment(), IFragment<Program> {
override lateinit var recyclerView: RecyclerView
override lateinit var itemList: ArrayList<Program>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_program_selector, container, false)
        initFragment()
        loadList(view)
        return view
    }

    private fun initFragment(){
        val b = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        b.isVisible = false

        val appBar: androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbar)
        appBar.title = getString(R.string.program_selector_title)

        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.PROGRAM
    }
    override fun loadList(view: View){
        itemList = arrayListOf<Program>()
        Database.db.collection("programok").get().addOnSuccessListener { result ->
            if(!result.isEmpty){
                for(doc in result){
                    itemList.add(Program(doc.data["ProgramNev"] as String))
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

    override fun setupView(view: View){

        recyclerView = view.findViewById(R.id.programSelectorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        val adapt = ProgramAdapter(itemList)
        recyclerView.adapter = adapt

        adapt.setOnItemClickListener(object : ProgramAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, program: Program) {
                CurrentState.programName = program.title

                requireActivity()
                    .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    .selectedItemId = R.id.install

            }
        })
    }

    override fun loadComments(view: View) {

    }
}

