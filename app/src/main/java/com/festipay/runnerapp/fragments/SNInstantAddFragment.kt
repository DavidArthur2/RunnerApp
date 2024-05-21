package com.festipay.runnerapp.fragments

import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CommentsAdapter
import com.festipay.runnerapp.adapters.SNAdapter
import com.festipay.runnerapp.adapters.SNAddAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.Program
import com.festipay.runnerapp.data.SN
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.BarcodeScanReceiver
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideKeyboard
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
import com.festipay.runnerapp.utilities.showWarning
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class SNInstantAddFragment : Fragment(), IFragment<SN> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<SN>

    private lateinit var modeName: String
    private lateinit var addButton: Button
    private lateinit var snInput: EditText
    private lateinit var adapt: SNAddAdapter
    private lateinit var context: FragmentActivity
    private var barcodeScanReceiver: BarcodeScanReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_s_n_add, container, false)
        initFragment()
        loadList(view)
        initViews(view)
        return view
    }

    private fun initFragment() {
        context = requireActivity()
        barcodeScanReceiver = BarcodeScanReceiver { barcode ->
            if (barcode != null) addSN(barcode)
            else Toast.makeText(context, "Sikertelen SN olvasás!", Toast.LENGTH_LONG).show()
        }
        val filter = IntentFilter("android.intent.ACTION_DECODE_DATA")
        context.registerReceiver(barcodeScanReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        CurrentState.operation = OperationType.SN_HANDLING
        modeName = Database.mapCollectionModeName()
        CurrentState.fragmentType = when (CurrentState.mode) {
            Mode.INSTALL -> FragmentType.INSTALL_COMPANY_SN_ADD
            Mode.DEMOLITION -> FragmentType.DEMOLITION_COMPANY_SN_ADD_INSTANT
            Mode.INVENTORY -> FragmentType.INVENTORY_ITEM_SN_ADD
            Mode.FINAL_INVENTORY -> FragmentType.FINAL_INVENTORY_ITEM_SN_ADD
            else -> FragmentType.INVENTORY_ITEM_SN_ADD
        }

    }

    private fun addSN(sn: String) {
        if (itemList.contains(SN(sn)))
            return showWarning(context, "'$sn' már a listában van!")
        saveItem(sn)
        hideKeyboard(context, snInput)
        snInput.text.clear()
        recyclerView.requestFocus()

    }

    private fun saveItem(sn: String) {
        showLoadingScreen(context)
        val data = hashMapOf(
            "SN" to sn
        )
        Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
            .collection("SN").add(data).addOnSuccessListener {
                itemList.add(SN(sn))
                adapt.notifyItemInserted(itemList.size - 1)
                Toast.makeText(context, "\'$sn\' hozzáadva!", Toast.LENGTH_SHORT).show()
                hideLoadingScreen()
            }

    }

    private fun initViews(view: View) {
        addButton = view.findViewById(R.id.snAddButton)
        snInput = view.findViewById(R.id.snInput)
        view.findViewById<FloatingActionButton>(R.id.snSaveFloatingActionButton).isVisible=false
        addButton.setOnClickListener {
            if (snInput.text.isNotEmpty())
                addSN(snInput.text.toString())

        }
    }

    override fun onViewLoaded() {
        Functions.hideLoadingScreen()
    }
    override fun loadList(view: View) {
        itemList = arrayListOf()
        Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
            .collection("SN").get().addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (doc in result) {
                        itemList.add(
                            SN(doc.data["SN"] as String)
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
        recyclerView = view.findViewById(R.id.snAddRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(false)


        adapt = SNAddAdapter(itemList)
        recyclerView.adapter = adapt

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapt.setOnItemClickListener(object : SNAddAdapter.OnItemDeleteListener {
            override fun onItemDelete(position: Int, snItem: SN) {
                showLoadingScreen(context)
                Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
                    .collection("SN").whereEqualTo("SN", snItem.sn).get().addOnSuccessListener{
                        if(it.documents.isNotEmpty())
                            Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
                                .collection("SN").document(it.documents[0].id).delete().addOnSuccessListener {
                                    Toast.makeText(context, "\'${snItem.sn}\' törölve!", Toast.LENGTH_SHORT).show()
                                    itemList.removeAt(position)
                                    adapt.notifyItemRemoved(position)
                                    hideLoadingScreen()
                                }
                    }.addOnFailureListener {
                        showError(context, "Hiba történt a törléskor!", it.toString())
                    }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        context.unregisterReceiver(barcodeScanReceiver)
    }


}