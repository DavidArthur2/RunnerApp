package com.festipay.runnerapp.utilities
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.showErrorDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private fun createDirectoryIfNotExist(directoryPath: String) {
    val directory = File(directoryPath)
    if (!directory.exists())directory.mkdirs()
}
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

    } catch (ex: Exception){
        print(ex.toString())
    }
}

fun showError(context:Context?, message: String, log: String = ""){
    hideLoadingScreen()
    if (context != null)
        showErrorDialog(context, message)

    logToFile("showError: $message $log")
}

fun showWarning(context:Context?, message: String){
    val toastMessage = SpannableString(message)
    toastMessage.setSpan(ForegroundColorSpan(Color.rgb(255,165,0)), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    Toast.makeText(context, "Figyelmeztetés: $toastMessage", Toast.LENGTH_LONG).show()
    logToFile("showWarning: $toastMessage")
}
