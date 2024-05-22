package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.festipay.runnerapp.R.*
import com.festipay.runnerapp.data.InstallFirstItemEnum
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.showError
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    /**
     * A login rész van a MainActivityben csak
     */

    private lateinit var loginButton: Button
    private lateinit var userInput: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()
        loginButton.setOnClickListener{
            loginClick()
        }

        checkVersion()
    }

    private fun initActivity(){
        setContentView(layout.activity_main)
        loginButton = findViewById(id.loginButton)
        val versionLabel: TextView = findViewById(id.versionLabel)
        userInput = findViewById(id.userInputText)

        versionLabel.text = "${getString(string.defaultVersionLabelString)}: ${packageManager.getPackageInfo(packageName, 0).versionName}"

        CurrentState.userName = null
        Database
    }


    private fun launchProgramSelector(){
        val nextActivity = Intent(this, SecondActivity::class.java)
        startActivity(nextActivity)
        finish()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        setupExitDialog()
    }

    private fun setupLoginErrorDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(layout.error_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(getDrawable(drawable.custom_error_button_bg))
        dialog.setCancelable(false)
        dialog.show()
        dialog.findViewById<Button>(id.back_button).setOnClickListener {
            hideLoadingScreen()
            dialog.dismiss()
        }
        dialog.setOnDismissListener{loginButton.isClickable = true}
    }
    private fun setupExitDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(layout.exit_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(getDrawable(drawable.custom_error_button_bg))
        dialog.setCancelable(false)
        dialog.show()
        dialog.findViewById<Button>(id.back_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<Button>(id.exit_button).setOnClickListener{
            exitProcess(0)
        }
        dialog.setOnDismissListener{loginButton.isClickable = true}
    }

    private fun loginClick(){
        showLoadingScreen(this)
        manipulateDB()
        val userName: String = userInput.text.toString()
        Database.db.collection("Users").whereEqualTo("Name", userName).get().addOnSuccessListener { result ->
            if(!result.isEmpty){
                CurrentState.userName = userName
                launchProgramSelector()
                userInput.clearComposingText()
            }
            else{
                setupLoginErrorDialog()
            }
        }.addOnFailureListener { exception ->
            showError(this, "Can't read documents in users: $exception")
        }
    }

    private fun manipulateDB(){
        val data = hashMapOf<String, Any>(
            "1" to InstallFirstItemEnum.NEM_KIRAKHATO
        )
        Database.db.collection("Company_Install").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val documentRef = Database.db.collection("Company_Install").document(document.id)
                    documentRef.update(data)
                }
            }

    }

    private fun checkVersion(){
        Database.db.collection("Config").get().addOnSuccessListener {
            val ver = it.documents[0].data?.get("MinVersion") as String
            val curr_ver = packageManager.getPackageInfo(packageName, 0).versionName
            if(ver != curr_ver.toString()) {
                loginButton.isEnabled = false
                showError(this, "A verziód elavult!\nA te verziód: $curr_ver\nJelenlegi verzió: $ver")
            }

        }.addOnFailureListener {
            loginButton.isEnabled = false
            showError(this, "Sikertelen verzió lekérés", it.toString())
        }
    }
}