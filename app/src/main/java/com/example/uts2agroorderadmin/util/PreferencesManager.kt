package com.example.uts2agroorderadmin.util

import android.content.Context
import androidx.core.content.edit

class PreferencesManager(context: Context) {
	private val prefs = context.getSharedPreferences("admin_prefs", Context.MODE_PRIVATE)

	fun saveToken(token: String) {
		prefs.edit { putString("token", token) }
	}

	fun getToken(): String? = prefs.getString("token", null)

	fun clear() = prefs.edit { clear() }
}