package com.smartpack.kernelprofiler.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.smartpack.kernelprofiler.R

/**
 * Created by Lennoard on May 29, 2020
 */

// This is how static methods work
object ViewUtilsKt {
    fun showDialog(manager: FragmentManager, fragment: DialogFragment) {
        val prev = manager.findFragmentByTag("dialog")
        manager.beginTransaction().also {
            if (prev != null) it.remove(prev)
            it.addToBackStack(null)
            fragment.show(it, "dialog")
        }
    }

    fun dismissDialog(manager: FragmentManager) {
        val ft = manager.beginTransaction()
        val fragment = manager.findFragmentByTag("dialog")
        if (fragment != null) {
            ft.remove(fragment).commit()
        }
    }
}

fun View.goAway() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.snackbar(message: String, @BaseTransientBottomBar.Duration length : Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, length).show()
}

fun Context.getColoredIcon(@DrawableRes drawableId: Int): Drawable {
    val drawable: Drawable = resources.getDrawable(drawableId, null)
    drawable.setTint(getThemeAccentColor())
    return drawable
}

fun Context.getSelectableBackground(): Drawable? {
    val typedArray = obtainStyledAttributes(intArrayOf(R.attr.selectableItemBackground))
    val drawable = typedArray.getDrawable(0)
    typedArray.recycle()
    return drawable
}

fun Context.getColorPrimaryColor(): Int {
    val value = TypedValue()
    theme.resolveAttribute(R.attr.colorPrimary, value, true)
    return value.data
}

fun Context.getThemeAccentColor(): Int {
    val value = TypedValue()
    theme.resolveAttribute(R.attr.colorAccent, value, true)
    return value.data
}

fun Context.dialogEditText(text: String?, inputType: Int = -1, onClick: (String) -> Unit) {
    val padding = resources.getDimension(R.dimen.dialog_padding).toInt()
    val layout = LinearLayout(this).apply {
        setPadding(padding, padding, padding, padding)
    }

    val editText = AppCompatEditText(this).apply {
        gravity = Gravity.CENTER
        layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        isSingleLine = true
        append(text ?: "")
        if (inputType >= 0) {
            this.inputType = inputType
        }

        layout.addView(this)
    }

    AlertDialog.Builder(this).apply {
        setView(layout)
        setNegativeButton(R.string.cancel) { _, _ ->  /* Do nothing */ }
        setPositiveButton(R.string.ok) { _, _ ->
            onClick(editText.text.toString())
        }
    }.show()
}