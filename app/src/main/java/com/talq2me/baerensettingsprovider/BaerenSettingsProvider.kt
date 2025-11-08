package com.talq2me.baerensettingsprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.core.content.edit
import com.talq2me.contract.SettingsContract // <-- I have corrected this import

class BaerenSettingsProvider : ContentProvider() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(): Boolean {
        prefs = context!!.getSharedPreferences("baeren_shared_settings", Context.MODE_PRIVATE)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val columns = arrayOf(
            SettingsContract.KEY_PROFILE,
            SettingsContract.KEY_PIN,
            SettingsContract.KEY_PARENT_EMAIL
        )
        val cursor = MatrixCursor(columns)

        cursor.addRow(arrayOf(
            prefs.getString(SettingsContract.KEY_PROFILE, "A"), // Default profile
            prefs.getString(SettingsContract.KEY_PIN, "1234"),    // Default PIN
            prefs.getString(SettingsContract.KEY_PARENT_EMAIL, "tiffany.quinlan@gmail.com")
        ))
        return cursor
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        var rowsAffected = 0
        values?.let {
            prefs.edit {
                if (it.containsKey(SettingsContract.KEY_PROFILE)) {
                    putString(SettingsContract.KEY_PROFILE, it.getAsString(SettingsContract.KEY_PROFILE))
                }
                if (it.containsKey(SettingsContract.KEY_PIN)) {
                    putString(SettingsContract.KEY_PIN, it.getAsString(SettingsContract.KEY_PIN))
                }
                // Add other keys as needed
            }
            rowsAffected = 1 // We are updating one "row" of settings
        }
        // Notify observers that the data has changed
        context?.contentResolver?.notifyChange(uri, null)
        return rowsAffected
    }

    // Other methods can be minimal for this use case
    override fun getType(uri: Uri): String = "vnd.android.cursor.dir/vnd.${SettingsContract.AUTHORITY}.settings"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
}