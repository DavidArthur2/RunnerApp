package com.festipay.runnerapp.utilities
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SdCardPath")
fun logToFile(message: String) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate = dateFormat.format(Date())
    val logFileName = "$todayDate.txt"
    val logFile = File("/storage/emulated/0/Android/data/com.cardnet.runnerapp/files/", logFileName) // Replace "path_to_your_directory" with the actual directory path where you want to store the log file

    val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    val timestamp = timestampFormat.format(Date())

    logFile.appendText("[$timestamp] $message\n")
}

fun showError(context:Context?, message: String){
    val toastMessage = SpannableString(message)
    toastMessage.setSpan(ForegroundColorSpan(Color.RED), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    Toast.makeText(context, "Hiba történt: $toastMessage", Toast.LENGTH_LONG).show()
    logToFile("showError: $toastMessage")
}

fun showWarning(context:Context?, message: String){
    val toastMessage = SpannableString(message)
    toastMessage.setSpan(ForegroundColorSpan(Color.rgb(255,165,0)), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    Toast.makeText(context, "Figyelmeztetés: $toastMessage", Toast.LENGTH_LONG).show()
    logToFile("showWarning: $toastMessage")
}
