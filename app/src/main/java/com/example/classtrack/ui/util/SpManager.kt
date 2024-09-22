package com.example.classtrack.ui.util

import android.content.Context


enum class Sp {
    USERNAME,
    PASSWORD
}

fun setSharedPreference(context: Context, key: Sp, value: String?) {
    val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    val edit = sharedPref.edit()
    edit.putString(key.toString(), value)
    edit.apply()
}

fun getSharedPreference(context: Context, key: Sp, defaultValue: String?): String? {
    return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        .getString(key.toString(), defaultValue)
}