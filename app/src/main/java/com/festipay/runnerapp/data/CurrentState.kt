package com.festipay.runnerapp.data

enum class Mode{
    INSTALL,
    DEMOLITION,
    INVENTORY
}

object CurrentState{
    var programName: String? = null
    var mode: Mode? = null
    var companySite: String? = null
    var userName: String? = null
}
