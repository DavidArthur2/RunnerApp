package com.festipay.runnerapp.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.festipay.runnerapp.R
import com.festipay.runnerapp.fragments.InventoryAddFragment

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
        try {
            CurrentState.loadingScreen?.dismiss()
        }catch (_:Exception){}
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

    fun showWarningDialog(context: FragmentActivity, message: String, action: Fragment? = null){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.warning_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.findViewById<TextView>(R.id.warning_dialog_message).text = message
        if(action == null)dialog.findViewById<Button>(R.id.ok_button).isVisible=false
        dialog.show()
        dialog.findViewById<Button>(R.id.back_button).setOnClickListener {
            dialog.dismiss()
        }
        if(action != null)dialog.findViewById<Button>(R.id.ok_button).setOnClickListener {
            showLoadingScreen(context)
            launchFragment(context, action)
            dialog.dismiss()
        }
        else{
            val back_button = dialog.findViewById<Button>(R.id.back_button)
            val layoutParams = back_button.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.horizontalBias = 0.5f
            back_button.layoutParams = layoutParams
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

    fun launchFragment(context: FragmentActivity, fragment: Fragment, final: Boolean = false){
        val args = Bundle()
        args.putString("final", "final")
        if(final)fragment.arguments = args


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