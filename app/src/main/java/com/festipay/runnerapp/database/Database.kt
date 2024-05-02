package com.festipay.runnerapp.database

import android.annotation.SuppressLint
import android.util.Log
import com.festipay.runnerapp.data.CompanyDemolition
import com.festipay.runnerapp.data.CompanyInstall
import com.festipay.runnerapp.data.Inventory
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

    fun getCompanyInstall() {
        val query = db.collection("telephely_telepites").whereEqualTo("TelephelyNev", "Bar 15")
        query.get().addOnSuccessListener { result ->
                for (document in result) {
                    val comp = mapCompanyInstall(document.data)

                }
            }
            .addOnFailureListener { exception ->
                //showError(this, "Can't read documents in getCompanyInstall: $exception")
            }

    }

    fun getProgramList(){
        db.collection("programok").get().addOnSuccessListener { result ->
            val programList = mutableListOf<String>()
            for (document in result) {
                programList.add(document.data["ProgramNev"] as String)
            }
        }
            .addOnFailureListener { exception ->
                //showError(this, "Can't read documents in getProgramList: $exception")
            }
    }

    fun mapCompanyInstall(firestoreData: Map<String, Any>): CompanyInstall {
        return CompanyInstall(
            kiadva = firestoreData["Kiadva"] as Boolean,
            nemKirakhato = firestoreData["NemKirakhato"] as Boolean,
            kirakva = firestoreData["Kirakva"] as Boolean,
            eloszto = firestoreData["Eloszto"] as Boolean,
            aram = firestoreData["Aram"] as Boolean,
            szoftver = firestoreData["Szoftver"] as Boolean,
            param = firestoreData["Param"] as Boolean,
            teszt = firestoreData["Teszt"] as Boolean,
            utolsoMegjegyzes = null,
            comments = null,
            didek = null
        )
    }

    fun mapCompanyDemolition(firestoreData: Map<String, Any>): CompanyDemolition {
        return CompanyDemolition(
            eszkozszam = firestoreData["Eszkozszam"] as Int,
            folyamatban = firestoreData["Folyamatban"] as Boolean,
            csomagolt = firestoreData["Csomagolt"] as Boolean,
            autoban = firestoreData["Autoban"] as Boolean,
            bazisLeszereles = firestoreData["BazisLeszereles"] as Boolean,
            utolsoMegjegyzes = null,
            comments = null,
            didek = null
        )
    }

    fun mapInventory(firestoreData: Map<String, Any>): Inventory {
        return Inventory(
            darabszam = firestoreData["Darabszam"] as Int,
            sn = firestoreData["SN"] as Boolean,
            targyNev = firestoreData["TargyNev"] as String,
            utolsoMegjegyzes = null,
            comments = null,
            didek = null
        )
    }
}