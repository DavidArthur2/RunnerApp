package com.festipay.runnerapp.data

import com.festipay.runnerapp.database.Database

class References {
    companion object {
        val users_ref = Database.db.collection("Users")
        val config_ref = Database.db.collection("Config")
    }
}