package com.festipay.runnerapp.utilities

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import com.festipay.runnerapp.R

object Functions {
    fun showLoadingScreen(context: Context){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.loading_screen)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()
        CurrentState.loadingScreen = dialog
    }
    fun hideLoadingScreen(){
        CurrentState.loadingScreen?.dismiss()
    }

    fun showErrorDialog(context: Context, message: String){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.error_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.findViewById<TextView>(R.id.error_dialog_message).text = message
        dialog.show()
        dialog.findViewById<Button>(R.id.back_button).setOnClickListener {
            dialog.dismiss()
        }
    }

    fun showInfoDialog(context: Context, title: String, message: String, buttonText: String = context.getString(R.string.error_dialog_back_button_text), hideLoading: Boolean = true){
        if(hideLoading)hideLoadingScreen()
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.info_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.findViewById<TextView>(R.id.info_dialog_message).text = message
        dialog.findViewById<TextView>(R.id.info_dialog_title).text = title
        dialog.findViewById<Button>(R.id.back_button).text = buttonText
        dialog.show()
        dialog.findViewById<Button>(R.id.back_button).setOnClickListener {
            dialog.dismiss()
        }
    }
}