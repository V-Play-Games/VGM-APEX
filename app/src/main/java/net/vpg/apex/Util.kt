package net.vpg.apex

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.unwrapActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.unwrapActivity()
    else -> throw IllegalStateException("Not an activity")
}