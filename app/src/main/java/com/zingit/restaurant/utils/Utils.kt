package com.zingit.restaurant.utils

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.seconds


object Utils {

     fun checkContactNumber(contact: String): Boolean {
        val expression = "^[6-9]\\d{9}$"
        val inputStr: CharSequence = contact
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(inputStr)
        return matcher.matches()
    }

    fun Fragment.setInteractionDisabled(disabled: Boolean) {
        if (disabled) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }


    }

    fun View.hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    }



     fun formatDate(firebaseTimestamp: Long): String {
        val date = Date(firebaseTimestamp)
        val inputFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return inputFormat.format(date)
    }
    fun formatTimeHHMM(firebaseTimestamp: Long): String {
        val date = Date(firebaseTimestamp)
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(date).toString().toUpperCase()
    }


}

