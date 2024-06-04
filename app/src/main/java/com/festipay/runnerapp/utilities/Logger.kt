package com.festipay.runnerapp.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.showErrorDialog
import com.festipay.runnerapp.utilities.Functions.showWarningDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SdCardPath")
fun logToFile(message: String) {
    try {
        Log.e("logToFileError:", message)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())
        val logFileName = "$todayDate.txt"
        val logFile = File(
            "/storage/emulated/0/Android/data/com.festipay.runnerapp/files/",
            logFileName
        )

        val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val timestamp = timestampFormat.format(Date())

        if (!logFile.exists()) logFile.createNewFile()
        logFile.appendText("[$timestamp] $message\n")

    } catch (ex: Exception) {
        print(ex.toString())
    }
}

fun showError(context: Context?, message: String, log: String = "") {
    hideLoadingScreen()
    if (context != null)
        showErrorDialog(context, message)

    logToFile("showError: $message | $log")
}

fun showWarning(context: FragmentActivity?, message: String, action: Fragment? = null) {
    if (context != null)
        return showWarningDialog(context, message, action)
    logToFile("showWarning: $message")
}
