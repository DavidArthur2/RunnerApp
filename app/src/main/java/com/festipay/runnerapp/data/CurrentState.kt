package com.festipay.runnerapp.data

import com.festipay.runnerapp.utilities.Fragment

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
    var fragment: Fragment? = null
}
