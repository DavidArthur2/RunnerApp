package com.festipay.runnerapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CompanyInstallAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.InstallFirstItemEnum
import com.festipay.runnerapp.data.InstallSecondItemEnum
import com.festipay.runnerapp.data.References.Companion.comments_ref
import com.festipay.runnerapp.data.References.Companion.mode_ref
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.DateFormatter.timestampToLocalDateTime
import com.festipay.runnerapp.utilities.Filter
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.InstallFilter
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.logToFile
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query


class InstallFragment : Fragment(), IFragment<CompanyInstall> {
    override lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompanyInstallAdapter
    override lateinit var itemList: ArrayList<CompanyInstall>
    private lateinit var filter: Filter<CompanyInstall>
    private lateinit var context: FragmentActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_install, container, false)

        initFragment()
        loadList(view)

        return view
    }

    fun initFragment() {
        context = requireActivity()
        CurrentState.mode = Mode.INSTALL
        CurrentState.fragmentType = com.festipay.runnerapp.utilities.FragmentType.INSTALL

        val bottomView = context
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomView.isVisible = true

        val appBar: Toolbar = context.findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.programName} - ${getString(R.string.install_title)}"
        setHasOptionsMenu(true)
    }

    override fun onViewLoaded() {
        filter = Filter(adapter, itemList)
        invokeFilter()
        hideLoadingScreen()
    }

    override fun loadList(view: View) {
        itemList = arrayListOf()
        mode_ref()
            .whereEqualTo("ProgramName", CurrentState.programName)
            .orderBy("CompanyName", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (doc in result) {
                        val lm = doc.data["LastModified"] as Timestamp?
                        itemList.add(
                            CompanyInstall(
                                companyName = doc.data["CompanyName"] as String,
                                firstItem = InstallFirstItemEnum.valueOf(doc.data["1"] as String),
                                secondItem = InstallSecondItemEnum.valueOf(doc.data["2"] as String),
                                thirdItem = doc.data["3"] as Boolean,
                                fourthItem = doc.data["4"] as Boolean,
                                fifthItem = doc.data["5"] as Boolean,
                                sixthItem = doc.data["6"] as Boolean,
                                seventhItem = doc.data["7"] as Boolean,
                                eightItem = doc.data["8"] as Boolean,
                                ninethItem = doc.data["9"] as Boolean,
                                docID = doc.id,
                                lastModified = timestampToLocalDateTime(lm)
                            )
                        )
                    }
                    loadComments(view)

                } else {
                    showError(context, "Nincs felvéve telephely!")
                }
            }.addOnFailureListener { exception ->
                showError(context, "Can't read documents in telephelyek: $exception")
            }
    }

    override fun loadComments(view: View) {
        for (it in itemList) {
            comments_ref().orderBy("Timestamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val comments: MutableList<Comment> = mutableListOf()
                        for (doc in result) {
                            comments.add(
                                Comment(
                                    megjegyzes = doc.data["Comment"] as String,
                                    megjegyzesIdo = timestampToLocalDateTime(doc.data["Timestamp"] as Timestamp)
                                )
                            )
                        }
                        it.lastComment = comments.last()
                        logToFile("COMMENTS LOG: ${it.lastComment}, ${comments.size}")
                    }
                    if (itemList.last() == it) setupView(view)
                }.addOnFailureListener {
                    showError(context, "Sikertelen volt a kommentek lehívása!")
                }
        }
    }

    override fun setupView(view: View) {

        recyclerView = view.findViewById(R.id.companyInstallRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)


        adapter = CompanyInstallAdapter(itemList)
        recyclerView.adapter = adapter

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


        adapter.setOnItemClickListener(object : CompanyInstallAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, companyInstall: CompanyInstall) {
                CurrentState.companySite = companyInstall.companyName
                CurrentState.companySiteID = companyInstall.docID
                Functions.launchFragment(context, OperationSelectorFragment())

            }

            override fun onPinGoClick(position: Int, companyInstall: CompanyInstall) {
                CurrentState.companySite = companyInstall.companyName
                CurrentState.companySiteID = companyInstall.docID
                val bundle = Bundle()
                bundle.putString("go", "go")
                val frag = GPSFragment()
                frag.arguments = bundle
                Functions.launchFragment(context, frag)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        val filterItem = menu.findItem(R.id.actionFilter)

        filterItem.setOnMenuItemClickListener {
            showFilterDialog()
            true
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter.filterList(text = newText ?: "")
                return true
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showFilterDialog() {
        val filterOptions = InstallFilter.toCharSequence()
        val selectedItems = Filter.selectedInstallItems.copyOf()

        val builder = AlertDialog.Builder(context)

        val dialogLayout = layoutInflater.inflate(R.layout.custom_filter_dialog_layout, null)

        val block1TitleTextView = dialogLayout.findViewById<TextView>(R.id.block1TitleTextView)
        val block1CheckboxGroup = dialogLayout.findViewById<LinearLayout>(R.id.block1CheckboxGroup)
        val block2TitleTextView = dialogLayout.findViewById<TextView>(R.id.block2TitleTextView)
        val block2CheckboxGroup = dialogLayout.findViewById<LinearLayout>(R.id.block2CheckboxGroup)
        val block3TitleTextView = dialogLayout.findViewById<TextView>(R.id.block3TitleTextView)
        val block3CheckboxGroup = dialogLayout.findViewById<LinearLayout>(R.id.block3CheckboxGroup)


        block1TitleTextView.text = "Felderítés"
        block2TitleTextView.text = "Telepítés"
        block3TitleTextView.text = "Elemek"

        for (i in filterOptions.indices) {
            val checkBox = CheckBox(context)
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            checkBox.text = filterOptions[i]
            checkBox.isChecked = selectedItems[i]
            val paddingInPixels = resources.getDimensionPixelSize(R.dimen.checkbox_padding)
            checkBox.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels)

            if (i in 0..2) {
                block1CheckboxGroup.addView(checkBox)
            } else if (i in 3..7) {
                block2CheckboxGroup.addView(checkBox)
            } else
                block3CheckboxGroup.addView(checkBox)
        }

        builder.setView(dialogLayout)
            .setPositiveButton("Szűrés") { _, _ ->
                val updatedSelectedItems = mutableListOf<Boolean>()
                for (i in 0 until block1CheckboxGroup.childCount) {
                    val checkBox = block1CheckboxGroup.getChildAt(i) as CheckBox
                    updatedSelectedItems.add(checkBox.isChecked)
                }
                for (i in 0 until block2CheckboxGroup.childCount) {
                    val checkBox = block2CheckboxGroup.getChildAt(i) as CheckBox
                    updatedSelectedItems.add(checkBox.isChecked)
                }
                for (i in 0 until block3CheckboxGroup.childCount) {
                    val checkBox = block3CheckboxGroup.getChildAt(i) as CheckBox
                    updatedSelectedItems.add(checkBox.isChecked)
                }
                invokeFilter(updatedSelectedItems.toBooleanArray())
            }
            .setNegativeButton("Mégse", null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun invokeFilter(rawSelectedItems: BooleanArray? = null) {
        val selectedItems: BooleanArray = rawSelectedItems ?: Filter.selectedInstallItems.copyOf()

        val selectedFilters: MutableList<InstallFilter> = mutableListOf()
        for (i in selectedItems.indices) {
            if (selectedItems[i]) {
                selectedFilters.add(InstallFilter.valueOfOrdinal(i))
            }
        }
        filter.filterList(option = selectedFilters)
        Filter.selectedInstallItems = selectedItems
    }

}