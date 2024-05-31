package com.festipay.runnerapp.utilities

import android.app.Dialog
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.festipay.runnerapp.R.*
import kotlin.system.exitProcess

class Dialogs {
    companion object {
        fun showExitDialog(context: FragmentActivity, loginButton: Button) {
            val dialog = Dialog(context)
            dialog.setContentView(layout.exit_dialog)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val drawable = ContextCompat.getDrawable(context, drawable.custom_error_button_bg)
            if (drawable != null) {
                dialog.window?.setBackgroundDrawable(drawable)
            }
            dialog.setCancelable(false)
            dialog.show()
            dialog.findViewById<Button>(id.back_button).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<Button>(id.exit_button).setOnClickListener {
                exitProcess(0)
            }
            dialog.setOnDismissListener { loginButton.isClickable = true }
        }

        fun showLoginErrorDialog(context: FragmentActivity, loginButton: Button) {
            val dialog = Dialog(context)
            dialog.setContentView(layout.error_dialog)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val drawable = ContextCompat.getDrawable(context, drawable.custom_error_button_bg)
            if (drawable != null) {
                dialog.window?.setBackgroundDrawable(drawable)
            }
            dialog.setCancelable(false)
            dialog.show()
            dialog.findViewById<Button>(id.back_button).setOnClickListener {
                Functions.hideLoadingScreen()
                dialog.dismiss()
            }
            dialog.setOnDismissListener { loginButton.isClickable = true }
        }

        fun showLogoutDialog(context: FragmentActivity, launchMain: () -> Unit) {
            val dialog = Dialog(context)
            dialog.setContentView(layout.exit_dialog)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val drawable = ContextCompat.getDrawable(context, drawable.custom_error_button_bg)
            if (drawable != null) {
                dialog.window?.setBackgroundDrawable(drawable)
            }
            dialog.findViewById<TextView>(id.custom_dialog_title)?.text =
                context.getString(string.logoutText)
            dialog.findViewById<TextView>(id.exit_dialog_message_label)?.text =
                context.getString(string.logoutConfirmText)

            dialog.setCancelable(false)
            dialog.show()

            dialog.findViewById<Button>(id.back_button).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<Button>(id.exit_button).setOnClickListener {
                launchMain()
            }
        }
    }

}