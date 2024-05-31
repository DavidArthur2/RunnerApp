package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.festipay.runnerapp.R.*
import com.festipay.runnerapp.data.References.Companion.users_ref
import com.festipay.runnerapp.data.References.Companion.config_ref
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.showError
import com.festipay.runnerapp.utilities.Dialogs.Companion.showExitDialog
import com.festipay.runnerapp.utilities.Dialogs.Companion.showLoginErrorDialog
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen

class MainActivity : AppCompatActivity() {
    /**
     * A login rész van a MainActivityben csak
     */

    private lateinit var loginButton: Button
    private lateinit var userInput: EditText
    private lateinit var versionLabel: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initActivity()
    }

    private fun initActivity() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)  // TURN OFF NIGHT MODE
        showLoadingScreen(this)
        CurrentState.userName = null

        try {
            Database
        } catch (ex: Exception) {
            showError(this, "Hiba történt a Firebase inicializálásakor!", ex.toString())
        }

        checkVersion()

    }

    private fun initViews() {
        setContentView(layout.activity_main)
        versionLabel = findViewById(id.versionLabel)
        userInput = findViewById(id.userInputText)
        loginButton = findViewById(id.loginButton)
        loginButton.isEnabled = false


        loginButton.setOnClickListener {
            loginClick()
        }
    }


    private fun launchProgramSelector() {
        val nextActivity = Intent(this, SecondActivity::class.java)
        startActivity(nextActivity)
        finish()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showExitDialog(this, loginButton)
    }


    private fun loginClick() {
        showLoadingScreen(this)
        val userName: String = userInput.text.toString()
        users_ref.whereEqualTo("Name", userName).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    CurrentState.userName = userName
                    launchProgramSelector()
                    userInput.clearComposingText()
                } else {
                    showLoginErrorDialog(this, loginButton)
                }
            }.addOnFailureListener { exception ->
                showError(this, "Sikertelen autentikáció!", exception.toString())
            }
    }

    @SuppressLint("SetTextI18n")
    private fun checkVersion() {
        val currentVersion = packageManager.getPackageInfo(packageName, 0).versionName
        versionLabel.text = "Verzió: $currentVersion"

        config_ref.get().addOnSuccessListener {
            if(it.documents.isEmpty()){
                showError(this, "Sikertelen verzió lekérdezés!\n Ellenőrízd az Internetet!")
                onViewLoaded()
                return@addOnSuccessListener
            }
            val correctVersion = it.documents[0].data?.get("MinVersion") as String
            if (correctVersion != currentVersion.toString()) {
                showError(
                    this,
                    "A verziód elavult!\nA te verziód: $currentVersion\nJelenlegi verzió: $correctVersion"
                )
            }
            loginButton.isEnabled = true
            onViewLoaded()

        }.addOnFailureListener {
            showError(this, "Sikertelen verzió lekérés", it.toString())
        }
    }

    private fun onViewLoaded(){
        hideLoadingScreen()
    }
}