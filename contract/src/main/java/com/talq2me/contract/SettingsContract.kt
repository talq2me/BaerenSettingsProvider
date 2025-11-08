package com.talq2me.contract

import android.net.Uri
import androidx.core.net.toUri

object SettingsContract {
    const val AUTHORITY = "com.talq2me.baeren.settingsprovider"
    val CONTENT_URI: Uri = "content://$AUTHORITY/settings".toUri()

    const val KEY_PROFILE = "user_profile"
    const val KEY_PIN = "parent_pin"
    const val KEY_PARENT_EMAIL = "parent_email"
}