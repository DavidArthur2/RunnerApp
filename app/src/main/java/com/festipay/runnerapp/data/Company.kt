package com.festipay.runnerapp.data

data class CompanyInstall(var kiadva: Boolean, var nemKirakhato: Boolean, var kirakva: Boolean,
                          val eloszto: Boolean, var aram: Boolean, var szoftver: Boolean,
                          var param: Boolean, var teszt:Boolean,
                          var utolsoMegjegyzes: Comment?,
                          var comments: Comments?, var didek: DIDS?)

data class CompanyDemolition(var eszkozszam: Int, var folyamatban: Boolean, var csomagolt: Boolean,
                             var autoban: Boolean, var bazisLeszereles: Boolean,
                             var utolsoMegjegyzes: Comment?,
                             var comments: Comments?, var didek: DIDS?)

data class Inventory(var darabszam: Int, var sn: Boolean, var targyNev: String, var utolsoMegjegyzes: Comment?,
                     var comments: Comments?, var didek: DIDS?)