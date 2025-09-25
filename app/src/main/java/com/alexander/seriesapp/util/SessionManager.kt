package com.alexander.seriesapp.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_EMAIL = "USER_EMAIL"
    }

    //  Guardar usuario
    fun saveUser(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    // Obtener usuario
    fun getUser(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    //  Borrar solo el usuario (por ejemplo al cerrar sesión)
    fun clearUser() {
        prefs.edit().remove(KEY_USER_EMAIL).apply()
    }

    // 👉 Borrar todo (por si guardas más cosas en el futuro)
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
