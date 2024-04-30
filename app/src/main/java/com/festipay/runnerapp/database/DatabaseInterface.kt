package com.festipay.runnerapp.database

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore

interface DatabaseInterface {

    fun readTest(db:FirebaseFirestore)

    fun writeTest(db:FirebaseFirestore):Boolean
}