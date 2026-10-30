package com.example.test2.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.test2.entities.Account

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_session"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_ACCOUNT_ID = "accountId"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
    }

    fun saveSession(account: Account) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_ACCOUNT_ID, account.id)
            putString(KEY_USERNAME, account.username)
            putString(KEY_ROLE, account.role)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getAccountId(): Long {
        return prefs.getLong(KEY_ACCOUNT_ID, -1L)
    }

    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: ""
    }

    fun getRole(): String {
        return prefs.getString(KEY_ROLE, "") ?: ""
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
