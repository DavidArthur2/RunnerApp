package com.festipay.runnerapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.festipay.runnerapp.R.*
import com.festipay.runnerapp.data.DemolitionFirstItemEnum
import com.festipay.runnerapp.data.DemolitionSecondItemEnum
import com.festipay.runnerapp.data.InstallFirstItemEnum
import com.festipay.runnerapp.data.InstallFourthItemEnum
import com.festipay.runnerapp.data.InstallSecondItemEnum
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.showError
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
        val userName: String = userInput.text.toString()
        Database.db.collection("felhasznalok").whereEqualTo("nev", userName).get().addOnSuccessListener { result ->
            if(!result.isEmpty){
                CurrentState.userName = userName
                launchProgramSelector()
                userInput.clearComposingText()
            }
            else{
                setupLoginErrorDialog()
            }
        }.addOnFailureListener { exception ->
            showError(this, "Can't read documents in felhasznalok: $exception")
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finish()
    }

}