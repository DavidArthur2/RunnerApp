package com.festipay.runnerapp.database

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class FirestoreDao : DatabaseInterface {
    override fun readTest(db:FirebaseFirestore) {
        data class User(
            val name: String,
            val age: Int,
            val city: String
        )

    }

    override fun writeTest(db: FirebaseFirestore): Boolean {
        // Assume you want to add a new user to the "users" collection
        val newUser = hashMapOf(
            "name" to "John Doe",
            "email" to "johndoe@example.com"
            // Add other fields as needed
        )

        // Add the new user document to the collection
        val writeTask: Task<Void> = db.collection("user1").document("newUserId").set(newUser)

        // Wait for the write operation to complete
        return try {
            Tasks.await(writeTask) // This will block until the task is complete
            true // Write operation successful
        } catch (e: Exception) {
            // Handle any errors
            false // Write operation failed
        }
    }

}