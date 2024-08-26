package com.emall.net.utils

import android.app.Activity
import android.widget.*
import java.util.regex.Pattern

object ViewUtils {

    internal var viewFinal: TextView? = null
    private val progressDialog = CustomProgressDialog()
    private val progressBar = ProgressBar()

    fun checkTextViewValidation(txtView: TextView, error: String?): Boolean {
        if (txtView.length() <= 0) {
            txtView.error = error
            viewFinal = txtView
            return false
        }
        return true
    }

    fun checkPasswordLength(txtView: TextView, error: String?): Boolean {
        if (txtView.length() in 1..7) {
            txtView.error = error
            viewFinal = txtView
            return false
        }
        return true
    }


    fun isPasswordMatched(
        textViewPassword: TextView,
        textViewConfirmPassword: TextView,
        error: String?,
    ): Boolean {
        val password = textViewPassword.text.toString()
        val confirmPassword = textViewConfirmPassword.text.toString()
        val isValid = password == confirmPassword
        if (!isValid) {
            textViewConfirmPassword.error = error
            viewFinal = textViewConfirmPassword
        }
        return isValid
    }

    fun firstLetterisNumber(textView: TextView, error: String?): Boolean {
        var isValid = true
        val username = textView.text.toString()
        if (username.isNotEmpty()) {
            if (username.substring(0, 1).matches("[0-9]".toRegex())) {
                textView.error = error
                isValid = false
            } else {
                isValid = true
            }
        }
        return isValid
    }

    fun isEmailValid(textView: TextView, error: String?): Boolean {
        var isValid = true
        val email = textView.text.toString()

        /*if (email.length() <= 0) {
            textView.setError("Please enter Email Id!");
            return isValid;
        }*/if (email.isNotEmpty()) {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val inputStr: CharSequence = email
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(inputStr)
            if (matcher.matches()) {
                isValid = true
            } else {
                textView.error = error
                viewFinal = textView
                isValid = false
            }
        }
        return isValid
    }


    fun showProgressDialog(activity: Activity) {
        progressDialog.show(activity)
    }

    fun hideProgressDialog() {
        progressDialog.dialog.dismiss()
    }

    fun showProgressBar(activity: Activity) {
        progressBar.show(activity)
    }

    fun hideProgressBar() {
        progressBar.dialog.dismiss()
    }

}