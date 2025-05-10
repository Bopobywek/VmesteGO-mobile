package ru.vmestego.utils

import android.content.Context
import android.widget.Toast

fun Context.showShortToast(text: String) {
    val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    toast.show()
}