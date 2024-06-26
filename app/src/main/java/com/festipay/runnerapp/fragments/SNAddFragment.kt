package com.festipay.runnerapp.fragments

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.SNAddAdapter
import com.festipay.runnerapp.data.References.Companion.sn_ref
import com.festipay.runnerapp.data.SN
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.BarcodeScanReceiver
import com.festipay.runnerapp.utilities.CamScanner
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideKeyboard
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
import com.festipay.runnerapp.utilities.showWarning
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SNAddFragment : Fragment(), IFragment<SN> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<SN>
    private lateinit var modeName: String
    private lateinit var addButton: Button
    private lateinit var snInput: EditText
    private lateinit var cameraButton: FloatingActionButton
    private lateinit var adapt: SNAddAdapter
    private lateinit var context: FragmentActivity
    private lateinit var camScanner: CamScanner
    private var hasScanner = false
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
            Mode.DEMOLITION -> FragmentType.DEMOLITION_COMPANY_SN_ADD
            Mode.INVENTORY -> FragmentType.INVENTORY_ITEM_SN_ADD
            Mode.FINAL_INVENTORY -> FragmentType.FINAL_INVENTORY_ITEM_SN_ADD
            else -> FragmentType.INVENTORY_ITEM_SN_ADD
        }
        camScanner = CamScanner(context, ::addSN)
    }

    private fun initViews(view: View) {
        addButton = view.findViewById(R.id.snAddButton)
        snInput = view.findViewById(R.id.snInput)

        cameraButton = view.findViewById(R.id.cameraFloatingActionButton)
        if (Build.MANUFACTURER == "Urovo") {
            hasScanner = true
            cameraButton.isVisible = false
        }

        addButton.setOnClickListener {
            if (snInput.text.isNotEmpty())
                addSN(snInput.text.toString())

        }
        cameraButton
            .setOnClickListener {
                camScanner.scanCode()
            }
        view.findViewById<FloatingActionButton>(R.id.snSaveFloatingActionButton)
            .setOnClickListener {
                if(itemList.size > 0)
                    saveList()
            }
    }

    private fun addSN(sn: String) {
        if (sn.isEmpty())
            return if (hasScanner) showWarning(context, "Üres sort olvastál be!")
            else Toast.makeText(context, "Üres sort olvastál be!", Toast.LENGTH_LONG).show()
        if (itemList.contains(SN(sn)))
            return if (hasScanner) showWarning(context, "'$sn' már a listában van!")
            else Toast.makeText(context, "'$sn' már a listában van!", Toast.LENGTH_LONG).show()

        itemList.add(SN(sn))
        hideKeyboard(context, snInput)
        adapt.notifyItemInserted(itemList.size - 1)
        snInput.text.clear()
        recyclerView.requestFocus()
        Toast.makeText(context, "\'$sn\' hozzáadva!", Toast.LENGTH_SHORT).show()
    }

    private fun saveList() {
        var unsuccessfulCount = itemList.size
        val unsuccessfulList = ArrayList(itemList)
        showLoadingScreen(context)
        for (i in itemList) {
            val data = hashMapOf(
                "SN" to i.sn
            )
            sn_ref().add(data).addOnSuccessListener {
                unsuccessfulCount--
                unsuccessfulList.remove(i)
                if (i == itemList.last()) {
                    launchFragment(context, SNFragment())
                    if (unsuccessfulCount == 0)
                        showInfoDialog(context, "Hozzáadás", "SN lista sikeresen hozzáadva!")
                    else
                        showError(
                            context,
                            "$unsuccessfulCount / $itemList SN sikertelenül hozzáadva!\n$unsuccessfulList"
                        )
                }
            }
        }
    }

    override fun onViewLoaded() {
        Functions.hideLoadingScreen()
    }

    override fun loadList(view: View) {
        itemList = arrayListOf()
        setupView(view)
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
                itemList.removeAt(position)
                adapt.notifyItemRemoved(position)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        context.unregisterReceiver(barcodeScanReceiver)
    }

    fun onBackCalled() {
        if (itemList.size > 0)
            showWarning(context, "Nem mentettél paraszt!!", SNFragment())
        else launchFragment(context, SNFragment())
    }

}