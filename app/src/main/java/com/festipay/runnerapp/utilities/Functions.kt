package com.festipay.runnerapp.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.festipay.runnerapp.R
import com.festipay.runnerapp.fragments.ProgramSelectorFragment

object Functions {
    fun showLoadingScreen(context: Context){
        if(CurrentState.loadingScreen != null)return
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
        CurrentState.loadingScreen = null
    }

    fun showErrorDialog(context: Context, message: String){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.error_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

    fun launchFragment(context: FragmentActivity, fragment: Fragment){
        showLoadingScreen(context)
        context.supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
    }

    fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}