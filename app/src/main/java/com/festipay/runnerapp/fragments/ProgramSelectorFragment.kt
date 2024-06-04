package com.festipay.runnerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.ProgramAdapter
import com.festipay.runnerapp.data.Program
import com.festipay.runnerapp.data.References.Companion.finalInventoryEnable_ref
import com.festipay.runnerapp.data.References.Companion.programs_ref
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.logToFile
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProgramSelectorFragment : Fragment(), IFragment<Program> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<Program>
    private lateinit var context: FragmentActivity
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

    private fun initFragment() {
        context = requireActivity()
        val b = context.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        b.isVisible = false

        val appBar: androidx.appcompat.widget.Toolbar = context.findViewById(R.id.toolbar)
        appBar.title = getString(R.string.program_selector_title)

        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.PROGRAM
    }

    override fun onViewLoaded() {
        hideLoadingScreen()
    }

    override fun loadList(view: View) {
        itemList = arrayListOf()
        programs_ref()
            .whereArrayContains("users", CurrentState.userName as String).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (doc in result) {
                        itemList.add(Program(doc.data["ProgramName"] as String))
                    }
                    setupView(view)
                } else {
                    showError(context, "Nincs felvéve program!")
                }
            }.addOnFailureListener { exception ->
                showError(context, "Can't read documents in programs: $exception")
            }
    }

    override fun setupView(view: View) {

        recyclerView = view.findViewById(R.id.programSelectorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)


        val adapt = ProgramAdapter(itemList)
        recyclerView.adapter = adapt


        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    onViewLoaded()
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (_: Exception) {
                } finally {
                    onViewLoaded()
                }
            }
        })


        adapt.setOnItemClickListener(object : ProgramAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, program: Program) {
                CurrentState.programName = program.title

                val bottomView =
                    context.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

                finalInventoryEnable_ref().whereEqualTo("ProgramName", program.title).get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            val finalInventoryEnabled = it.documents[0].data?.get("enabled") as Boolean
                            bottomView.menu.findItem(R.id.finalInventory).isVisible = finalInventoryEnabled
                        }
                    }.addOnFailureListener {
                    logToFile(it.toString())
                }.addOnCompleteListener {
                    bottomView.selectedItemId = R.id.install
                }

            }
        })

    }

    override fun loadComments(view: View) {

    }
}

