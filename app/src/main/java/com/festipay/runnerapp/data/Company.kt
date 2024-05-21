package com.festipay.runnerapp.data

data class CompanyInstall(var companyName: String,
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
                          var lastComment: Comment? = null)

data class CompanyDemolition(var companyName: String,
                             val firstItem: DemolitionFirstItemEnum,
                             val secondItem: DemolitionSecondItemEnum,
                             val thirdItem: Boolean,
                             var docID: String = "",
                             var lastComment: Comment? = null)
enum class InstallFirstItemEnum(private var displayName: String){
    NEM_KIRAKHATO("Nem kirakható"),
    KIRAKHATO("Kirakható"),
    TELEPITHETO("Telepíthető");

    override fun toString(): String {
        return displayName
    }
}

enum class InstallSecondItemEnum(private var displayName: String){
    STATUSZ_NELKUL("Státusz nélkül"),
    BAZIS_KIADVA("Bázison kiadva"),
    KIHELYEZESRE_VAR("Kihelyezésre vár"),
    KIRAKVA("Kirakva"),
    HELYSZINEN_TESZTELVE("Helyszínen tesztelve");

    override fun toString(): String {
        return displayName
    }
}
enum class DemolitionFirstItemEnum(private var displayName: String){
    BONTHATO("Bontható"),
    BONTASRA_VAR("Bontásra vár"),
    STATUSZ_NELKUL("Státusz nélkül");

    override fun toString(): String {
        return displayName
    }
}

enum class DemolitionSecondItemEnum(private var displayName: String){
    CSOMAGOLT_TEREPEN("Csomagolt terepen"),
    ELSZALLITVA("Elszállítva"),
    STATUSZ_NELKUL("Státusz nélkül");

    override fun toString(): String {
        return displayName
    }
}
