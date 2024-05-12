package com.festipay.runnerapp.data

data class Inventory(var darabszam: Int, var sn: Boolean, var targyNev: String, var utolsoMegjegyzes: Comment?,
                     var comments: Comments?, var didek: SN?, var docID: String = "")
