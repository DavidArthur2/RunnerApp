package com.festipay.runnerapp.data

import java.time.LocalDateTime

data class CompanyInstall(
    var companyName: String,
    val firstItem: InstallFirstItemEnum,
    val secondItem: InstallSecondItemEnum,
    val thirdItem: Boolean,
    val fourthItem: Boolean,
    val fifthItem: Boolean,
    val sixthItem: Boolean,
    val seventhItem: Boolean,
    val eightItem: Boolean,
    val ninethItem: Boolean,
    var docID: String = "",
    var lastComment: Comment? = null,
    var lastModified: LocalDateTime? = null,
    var quantity: Int = 0,
    var companyCode: String = "",
    var lastAdded: LocalDateTime? = null
) : IData

data class CompanyDemolition(
    var companyName: String,
    val firstItem: DemolitionFirstItemEnum,
    val secondItem: DemolitionSecondItemEnum,
    val thirdItem: Boolean,
    var docID: String = "",
    var lastComment: Comment? = null,
    var lastModified: LocalDateTime? = null
) : IData


enum class InstallFirstItemEnum(private var displayName: String) {
    NEM_KIRAKHATO("Nem kirakható"),
    KIRAKHATO("Kirakható"),
    TELEPITHETO("Telepíthető");

    override fun toString(): String {
        return displayName
    }
}


enum class InstallSecondItemEnum(private var displayName: String) {
    STATUSZ_NELKUL("Státusz nélkül"),
    BAZIS_KIADVA("Bázison kiadva"),
    KIHELYEZESRE_VAR("Kihelyezésre vár"),
    KIRAKVA("Kirakva"),
    HELYSZINEN_TESZTELVE("Helyszínen tesztelve");

    override fun toString(): String {
        return displayName
    }
}

enum class DemolitionFirstItemEnum(private var displayName: String) {
    BONTHATO("Bontható"),
    MEG_NYITVA("Még nyitva"),
    NEM_HOZZAFERHETO("Nem hozzáférhető");

    override fun toString(): String {
        return displayName
    }
}

enum class DemolitionSecondItemEnum(private var displayName: String) {
    CSOMAGOLVA("Csomagolva"),
    SZALLITASRA_VAR("Szállításra vár"),
    ELSZALLITVA("Elszállítva"),
    STATUSZ_NELKUL("Státusz nélkül");

    override fun toString(): String {
        return displayName
    }
}
