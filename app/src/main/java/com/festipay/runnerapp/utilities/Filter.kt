package com.festipay.runnerapp.utilities

import com.festipay.runnerapp.adapters.CompanyDemolitionAdapter
import com.festipay.runnerapp.adapters.CompanyInstallAdapter
import com.festipay.runnerapp.adapters.IAdapter
import com.festipay.runnerapp.adapters.InventoryAdapter
import com.festipay.runnerapp.adapters.SNAdapter
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.DemolitionFirstItemEnum
import com.festipay.runnerapp.data.DemolitionSecondItemEnum
import com.festipay.runnerapp.data.DemolitionSecondItemEnum.*
import com.festipay.runnerapp.data.InstallFirstItemEnum.*
import com.festipay.runnerapp.data.InstallSecondItemEnum
import com.festipay.runnerapp.data.InstallSecondItemEnum.*
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.data.SN


class Filter<T>(var adapter: IAdapter, var itemList: ArrayList<T>) {

    companion object {
        var selectedInstallItems: BooleanArray = BooleanArray(InstallFilter.toCharSequence().size)
        var selectedDemolitionItems: BooleanArray =
            BooleanArray(DemolitionFilter.toCharSequence().size)
    }


    fun filterList(text: String = "", option: MutableList<out IFilter> = mutableListOf()) {

        when (CurrentState.fragmentType) {
            FragmentType.INSTALL -> {
                val adapter = (this.adapter as CompanyInstallAdapter)
                val itemList = (this.itemList as ArrayList<CompanyInstall>)
                var filteredList: List<CompanyInstall> = itemList

                selectedInstallItems.fill(false)
                if (option.isEmpty() && text.isNotEmpty()) {
                    val onlyCodeSearch = text.toCharArray()[0] == '*'
                    filteredList = filteredList.filter {
                        if (onlyCodeSearch)
                            it.companyCode.lowercase().contains(text.lowercase())
                        else
                            it.companyName.lowercase()
                                .contains(text.lowercase()) || it.companyCode.lowercase()
                                .contains(text.lowercase())
                    }
                } else if (option.isNotEmpty()) {
                    for (filterOption in option) {
                        selectedInstallItems[(filterOption as InstallFilter).ordinal] = true
                        filteredList = filteredList.filter {
                            when (filterOption) {
                                InstallFilter.NINCS_ELOSZTO -> !it.thirdItem
                                InstallFilter.NINCS_ARAM -> !it.fourthItem
                                InstallFilter.NINCS_HALOZAT -> !it.fifthItem
                                InstallFilter.NINCS_PTG -> !it.sixthItem
                                InstallFilter.NINCS_SZOFTVER -> !it.seventhItem
                                InstallFilter.NINCS_PARAM -> !it.eightItem
                                InstallFilter.NINCS_HELYSZIN -> !it.ninethItem
                                InstallFilter.TELEPITHETO -> it.firstItem == TELEPITHETO
                                InstallFilter.KIRAKHATO -> it.firstItem == KIRAKHATO
                                InstallFilter.NEM_KIRAKHATO -> it.firstItem == NEM_KIRAKHATO
                                InstallFilter.KIADVA -> it.secondItem == BAZIS_KIADVA
                                InstallFilter.KIHELYEZESRE_VAR -> it.secondItem == KIHELYEZESRE_VAR
                                InstallFilter.STATUSZ_NELKUL -> it.secondItem == InstallSecondItemEnum.STATUSZ_NELKUL
                                InstallFilter.KIRAKVA -> it.secondItem == KIRAKVA
                                InstallFilter.HELYSZINEN_TESZTELVE -> it.secondItem == HELYSZINEN_TESZTELVE

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
                } else if (option.isNotEmpty()) {
                    for (filterOption in option) {
                        selectedDemolitionItems[(filterOption as DemolitionFilter).ordinal] = true
                        filteredList = filteredList.filter {
                            when (filterOption) {
                                DemolitionFilter.ELSZALLITVA -> it.secondItem == ELSZALLITVA
                                DemolitionFilter.CSOMAGOLVA -> it.secondItem == CSOMAGOLVA
                                DemolitionFilter.MEG_NYITVA -> it.firstItem == DemolitionFirstItemEnum.MEG_NYITVA
                                DemolitionFilter.NEM_HOZZAFERHETO -> it.firstItem == DemolitionFirstItemEnum.NEM_HOZZAFERHETO
                                DemolitionFilter.SZALLITASRA_VAR -> it.secondItem == SZALLITASRA_VAR
                                DemolitionFilter.BONTHATO -> it.firstItem == DemolitionFirstItemEnum.BONTHATO
                                DemolitionFilter.STATUSZ_NELKUL -> it.secondItem == DemolitionSecondItemEnum.STATUSZ_NELKUL
                                DemolitionFilter.BAZIS_LESZERELES -> it.thirdItem

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

            FragmentType.INSTALL_COMPANY_SN, FragmentType.DEMOLITION_COMPANY_SN, FragmentType.INVENTORY_ITEM_SN, FragmentType.FINAL_INVENTORY_ITEM_SN -> {
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
    TELEPITHETO("Telepíthető"),
    KIRAKHATO("Kirakható"),
    NEM_KIRAKHATO("Nem kirakható"),
    KIADVA("Bázis kiadva"),
    KIHELYEZESRE_VAR("Kihelyezésre vár"),
    KIRAKVA("Kirakva"),
    HELYSZINEN_TESZTELVE("Helyszínen tesztelve"),
    STATUSZ_NELKUL("Státusz nélkül"),
    NINCS_PARAM("Hiányzik: Param"),
    NINCS_ELOSZTO("Hiányzik: Elosztó"),
    NINCS_ARAM("Hiányzik: Áram"),
    NINCS_HALOZAT("Hiányzik: Hálozat"),
    NINCS_PTG("Hiányzik: Pénztárgép"),
    NINCS_SZOFTVER("Hiányzik: Szoftver"),
    NINCS_HELYSZIN("Hiányzik: Helyszín");

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
    BONTHATO("Bontható"),
    MEG_NYITVA("Még nyitva"),
    NEM_HOZZAFERHETO("Nem hozzáférhető"),
    CSOMAGOLVA("Csomagolva"),
    SZALLITASRA_VAR("Szállításra vár"),
    ELSZALLITVA("Elszállítva"),
    STATUSZ_NELKUL("Státusz nélkül"),
    BAZIS_LESZERELES("Bázis leszerelés kell");

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
