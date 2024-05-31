package com.festipay.runnerapp.data

import android.provider.ContactsContract.Data
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

class References {
    companion object {
        val users_ref = Database.db.collection("Users")
        val config_ref = Database.db.collection("Config")
        val coord_ref = Database.db.collection("Coordinates")
        val programs_ref = Database.db.collection("Programs")
        val finalInventoryEnable_ref = Database.db.collection("Final_Inventory_enable")

        val comments_ref = Database.db.collection(CurrentState.mode.toString()).document(CurrentState.companySiteID ?: "").collection("Comments")

        val mode_ref = Database.db.collection(CurrentState.mode.toString())
        val company_ref = mode_ref.document(CurrentState.companySiteID ?: "")
        val sn_ref = company_ref.collection("SN")

        fun comments_ref(companySiteID: String): CollectionReference {
            return Database.db.collection(CurrentState.mode.toString()).document(companySiteID).collection("Comments")
        }

        fun sn_ref(snID: String): DocumentReference{
            return sn_ref.document(snID)
        }

    }
}