package com.festipay.runnerapp.utilities

import android.app.Dialog

enum class Mode(private var displayName: String) {
    INSTALL("Company_Install"),
    DEMOLITION("Company_Demolition"),
    INVENTORY("Inventory"),
    FINAL_INVENTORY("Final_Inventory");

    override fun toString(): String {
        return displayName
    }
}

object CurrentState {
    var programName: String? = null
    var mode: Mode? = null
    var companySite: String? = null
    var companySiteID: String? = null
    var userName: String? = null
    var operation: OperationType? = null
    var fragmentType: FragmentType? = null
    var loadingScreen: Dialog? = null
}
