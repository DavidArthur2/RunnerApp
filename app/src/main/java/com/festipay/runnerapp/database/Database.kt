package com.festipay.runnerapp.database

import android.annotation.SuppressLint
import com.festipay.runnerapp.utilities.logToFile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Database {
    private var database: DatabaseInterface = FirestoreDao()
    private val USED_DATABASE: String = "Firestore"
    @SuppressLint("StaticFieldLeak")
    lateinit var db: FirebaseFirestore

    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {

        database = when (USED_DATABASE) {
            "Firestore" -> {
                db = Firebase.firestore
                FirestoreDao()
            }
            else -> {
                logToFile("Invalid database implementation: $USED_DATABASE")
                db = Firebase.firestore
                FirestoreDao()
            }
        }
    }

    fun readTest() {
        database.readTest(db)
    }

    fun writeTest() {
        database.writeTest(db)
    }

}