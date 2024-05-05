package com.festipay.runnerapp.data

data class Inventory(var darabszam: Int, var sn: Boolean, var targyNev: String, var utolsoMegjegyzes: Comment?,
                     var comments: Comments?, var didek: DIDS?, var docID: String = "")
