package com.zingit.restaurant.utils

import android.view.WindowManager
import androidx.fragment.app.Fragment
import java.util.regex.Matcher
import java.util.regex.Pattern

fun checkContactNumber(contact:String):Boolean
{
    val expression = "^[6-9]\\d{9}$"
    val inputStr: CharSequence = contact
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(inputStr)
    return matcher.matches()
}

fun Fragment.setInteractionDisabled(disabled : Boolean) {
    if (disabled) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    } else {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}