package com.festipay.runnerapp.database

import android.annotation.SuppressLint
import android.util.Log
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.Inventory
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.logToFile
import com.festipay.runnerapp.utilities.showError
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Database {
    @SuppressLint("StaticFieldLeak")
    lateinit var db: FirebaseFirestore

    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {
        db = Firebase.firestore
    }

    fun mapCollectionModeName(): String {
        return when (CurrentState.mode) {
            Mode.INSTALL -> {
                "Company_Install"
            }

            Mode.DEMOLITION -> {
                "Company_Demolition"
            }

            Mode.INVENTORY -> {
                "Inventory"
            }
            Mode.FINAL_INVENTORY -> {
                "Final_Inventory"
            }

            else -> {
                "Inventory"
            }
        }
    }

}