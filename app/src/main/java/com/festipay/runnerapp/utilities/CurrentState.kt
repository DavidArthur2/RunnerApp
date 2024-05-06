package com.festipay.runnerapp.utilities

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
    var fragmentType: FragmentType? = null
}
