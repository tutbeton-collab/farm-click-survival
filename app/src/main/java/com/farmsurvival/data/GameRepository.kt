package com.farmsurvival.data

import android.content.Context
import android.content.SharedPreferences
import com.farmsurvival.domain.model.GameState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface GameRepository {
    fun saveGame(state: GameState)
    fun loadGame(): GameState?
    fun clearGame()
}

class SharedPrefGameRepository(context: Context) : GameRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("farm_survival_save", Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    companion object {
        private const val KEY_GAME_STATE = "game_state"
        private const val KEY_HAS_SAVE = "has_save"
    }

    override fun saveGame(state: GameState) {
        try {
            val serialized = json.encodeToString(state)
            prefs.edit()
                .putString(KEY_GAME_STATE, serialized)
                .putBoolean(KEY_HAS_SAVE, true)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loadGame(): GameState? {
        return try {
            val serialized = prefs.getString(KEY_GAME_STATE, null) ?: return null
            json.decodeFromString<GameState>(serialized)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun clearGame() {
        prefs.edit()
            .remove(KEY_GAME_STATE)
            .putBoolean(KEY_HAS_SAVE, false)
            .apply()
    }

    fun hasSavedGame(): Boolean = prefs.getBoolean(KEY_HAS_SAVE, false)
}
