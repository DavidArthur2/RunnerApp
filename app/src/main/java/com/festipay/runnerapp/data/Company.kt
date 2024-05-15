package com.festipay.runnerapp.data

data class CompanyInstall(var companyName: String,
                          val firstItem: InstallFirstItemEnum,
                          val secondItem: InstallSecondItemEnum,
                          val thirdItem: Boolean,
                          val fourthItem: InstallFourthItemEnum,
                          val fifthItem: Boolean,
                          val sixthItem: Boolean,
                          val seventhItem: Boolean,
                          val eightItem: Boolean,
                          val ninethItem: Boolean,
                          val tenthItem: Boolean,
                          val eleventhItem: Boolean,
                          var docID: String = "")

data class CompanyDemolition(var companyName: String,
                             val firstItem: DemolitionFirstItemEnum,
                             val secondItem: DemolitionSecondItemEnum,
                             val thirdItem: Boolean,
                             var docID: String = "")
enum class InstallFirstItemEnum{
    TELEPITHETO,
    KIRAKHATO,
    NEM_KIRAKHATO,
    STATUSZ_NELKUL
}

enum class InstallSecondItemEnum{
    KIADVA,
    KIHELYEZESRE_VAR,
    STATUSZ_NELKUL
}

enum class InstallFourthItemEnum{
    HELYSZINEN_TESZTELVE,
    MINOSEG_ELLENORZES,
    HASZNALATRA_KESZ,
    STATUSZ_NELKUL
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
