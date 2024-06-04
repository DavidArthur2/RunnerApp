package com.festipay.runnerapp.data

import android.provider.ContactsContract.Data
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

class References {
    companion object {
        fun users_ref(): CollectionReference {return Database.db.collection("Users")}
        fun config_ref(): CollectionReference {return Database.db.collection("Config")}
        fun coord_ref(): CollectionReference {return Database.db.collection("Coordinates")}
        fun programs_ref(): CollectionReference {return Database.db.collection("Programs")}
        fun finalInventoryEnable_ref(): CollectionReference {return Database.db.collection("Final_Inventory_enable")}
        fun comments_ref(): CollectionReference {return Database.db.collection(CurrentState.mode.toString()).document(CurrentState.companySiteID ?: "").collection("Comments")}
        fun mode_ref(): CollectionReference {return Database.db.collection(CurrentState.mode.toString())}
        fun company_ref(): DocumentReference {return mode_ref().document(CurrentState.companySiteID ?: "")}
        fun sn_ref(): CollectionReference {return company_ref().collection("SN")}

        fun comments_ref(companySiteID: String): CollectionReference {
            return Database.db.collection(CurrentState.mode.toString()).document(companySiteID).collection("Comments")
        }
        fun sn_ref(snID: String): DocumentReference{
            return sn_ref().document(snID)
        }

    }
}