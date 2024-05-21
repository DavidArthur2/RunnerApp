package com.festipay.runnerapp.utilities

import com.festipay.runnerapp.adapters.CompanyDemolitionAdapter
import com.festipay.runnerapp.adapters.CompanyInstallAdapter
import com.festipay.runnerapp.adapters.IAdapter
import com.festipay.runnerapp.adapters.InventoryAdapter
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.InstallFirstItemEnum.*
import com.festipay.runnerapp.data.InstallSecondItemEnum.*


class Filter<T>(var adapter: IAdapter, var itemList: ArrayList<T>) {

    fun filter(text: String = "", option: String = "") {

        when (CurrentState.fragmentType) {
            FragmentType.INSTALL -> {
                val adapter = (this.adapter as CompanyInstallAdapter)
                val itemList = (this.itemList as ArrayList<CompanyInstall>)
                var filteredList: List<CompanyInstall> = emptyList()

                filteredList = if (option == "") {
                    itemList.filter {
                        it.companyName.lowercase().contains(text.lowercase())
                    }
                } else {
                    itemList.filter {
                        when (option) {
                            "Nincs param" -> !it.eightItem

                            "Telepítésre vár" -> it.firstItem == TELEPITHETO && it.secondItem == KIRAKVA

                            else -> true
                        }
                    }
                }
                adapter.filterList(filteredList)
            }

            else -> {
            }
        }


    }
}

