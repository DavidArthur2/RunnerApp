package com.festipay.runnerapp.utilities

import androidx.fragment.app.Fragment
import com.festipay.runnerapp.adapters.CompanyDemolitionAdapter
import com.festipay.runnerapp.adapters.CompanyInstallAdapter
import com.festipay.runnerapp.adapters.IAdapter
import com.festipay.runnerapp.adapters.InventoryAdapter
import com.festipay.runnerapp.adapters.SNAdapter
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.DemolitionSecondItemEnum.*
import com.festipay.runnerapp.data.IData
import com.festipay.runnerapp.data.InstallFirstItemEnum.*
import com.festipay.runnerapp.data.InstallSecondItemEnum.*
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.data.SN


class Filter<T>(var adapter: IAdapter, var itemList: ArrayList<T>) {

    var selectedInstallItems: BooleanArray = BooleanArray(InstallFilter.toCharSequence().size)
    var selectedDemolitionItems: BooleanArray = BooleanArray(DemolitionFilter.toCharSequence().size)

    fun filterList(text: String = "", option: MutableList<out IFilter> = mutableListOf()) {

        when (CurrentState.fragmentType) {
            FragmentType.INSTALL -> {
                val adapter = (this.adapter as CompanyInstallAdapter)
                val itemList = (this.itemList as ArrayList<CompanyInstall>)
                var filteredList: List<CompanyInstall> = itemList

                selectedInstallItems.fill(false)
                if (option.isEmpty() && text.isNotEmpty()) {
                    filteredList = filteredList.filter {
                        it.companyName.lowercase().contains(text.lowercase())
                    }
                }
                else if(option.isNotEmpty())
                {
                    for (filterOption in option)
                    {
                        selectedInstallItems[(filterOption as InstallFilter).ordinal] = true
                        filteredList = filteredList.filter {
                            when (filterOption) {
                                InstallFilter.NINCS_PARAM -> it.eightItem

                                InstallFilter.TELEPITESRE_VAR -> it.firstItem == TELEPITHETO && it.secondItem == KIRAKVA

                            }
                        }
                    }
                }
                adapter.filterList(filteredList)
            }

            FragmentType.DEMOLITION -> {
                val adapter = (this.adapter as CompanyDemolitionAdapter)
                val itemList = (this.itemList as ArrayList<CompanyDemolition>)
                var filteredList: List<CompanyDemolition> = itemList

                selectedDemolitionItems.fill(false)
                if (option.isEmpty() && text.isNotEmpty()) {
                    filteredList = filteredList.filter {
                        it.companyName.lowercase().contains(text.lowercase())
                    }
                }
                else if(option.isNotEmpty())
                {
                    for (filterOption in option)
                    {
                        selectedDemolitionItems[(filterOption as DemolitionFilter).ordinal] = true
                        filteredList = filteredList.filter {
                            when (filterOption) {
                                DemolitionFilter.ELSZALLITANDO -> it.secondItem == CSOMAGOLT_TEREPEN

                            }
                        }
                    }
                }
                adapter.filterList(filteredList)
            }

            FragmentType.INVENTORY, FragmentType.FINAL_INVENTORY -> {
                val adapter = (this.adapter as InventoryAdapter)
                val itemList = (this.itemList as ArrayList<Inventory>)
                var filteredList: List<Inventory> = itemList

                if (option.isEmpty() && text.isNotEmpty()) {
                    filteredList = filteredList.filter {
                        it.itemName.lowercase().contains(text.lowercase())
                    }
                }
                adapter.filterList(filteredList)

            }

            FragmentType.INSTALL_COMPANY_SN, FragmentType.DEMOLITION_COMPANY_SN, FragmentType.INVENTORY_ITEM_SN, FragmentType.FINAL_INVENTORY_ITEM_SN ->
            {
                val adapter = (this.adapter as SNAdapter)
                val itemList = (this.itemList as ArrayList<SN>)
                var filteredList: List<SN> = itemList

                if (option.isEmpty() && text.isNotEmpty()) {
                    filteredList = filteredList.filter {
                        it.sn.lowercase().contains(text.lowercase())
                    }
                }
                adapter.filterList(filteredList)
            }

            else -> {

            }
        }


    }


}

enum class InstallFilter(private val displayName: String) : IFilter {
    NINCS_PARAM("Hiányzik: Param"),
    TELEPITESRE_VAR("Telepítésre vár");
    override fun toString(): String {
        return displayName
    }

    companion object {
        fun toCharSequence(): Array<String> {
            return entries.map { it.displayName }.toTypedArray()
        }

        fun valueOfOrdinal(idx: Int): InstallFilter {
            return entries[idx]
        }
    }

}

enum class DemolitionFilter(private val displayName: String) : IFilter {
    ELSZALLITANDO("Elszállítandó");
    override fun toString(): String {
        return displayName
    }

    companion object {
        fun toCharSequence(): Array<String> {
            return entries.map { it.displayName }.toTypedArray()
        }

        fun valueOfOrdinal(idx: Int): DemolitionFilter {
            return entries[idx]
        }
    }

}
