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
enum class InstallFirstItemEnum{
    NEM_KIRAKHATO,
    KIRAKHATO,
    TELEPITHETO,
}

enum class InstallSecondItemEnum{
    STATUSZ_NELKUL,
    BAZIS_KIADVA,
    KIHELYEZESRE_VAR,
    KIRAKVA,
    HELYSZINEN_TESZTELVE
}
enum class DemolitionFirstItemEnum{
    BONTHATO,
    BONTASRA_VAR,
    STATUSZ_NELKUL
}

enum class DemolitionSecondItemEnum{
    CSOMAGOLT_TEREPEN,
    ELSZALLITVA,
    STATUSZ_NELKUL
}
