package com.app.expensemanager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpensePreferences(private val context: Context) {

    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "expense_pref")
        private val keyUsername = stringPreferencesKey(name = "username")
        private val keyAccessToken = stringPreferencesKey(name = "access_token")
    }


    suspend fun saveUser(username: String) {
        context.dataStore.edit { preferences ->
            preferences[keyUsername] = username
        }
    }

    val currentUser: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[keyUsername]
        }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[keyAccessToken] = token
        }
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[keyAccessToken]

    }

    suspend fun clearData(){
        context.dataStore.edit {
            it.clear()
        }
    }
}