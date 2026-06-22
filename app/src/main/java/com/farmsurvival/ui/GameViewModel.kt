package com.farmsurvival.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.farmsurvival.data.GameRepository
import com.farmsurvival.domain.engine.GameEngine
import com.farmsurvival.domain.model.GameAction
import com.farmsurvival.domain.model.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _gameState = MutableStateFlow(GameEngine.createInitialState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGame()
    }

    private fun loadGame() {
        viewModelScope.launch {
            val saved = repository.loadGame()
            if (saved != null) {
                _gameState.value = saved
            }
            _isLoading.value = false
        }
    }

    fun performAction(action: GameAction) {
        val current = _gameState.value
        if (current.isGameOver) return

        val result = GameEngine.processAction(current, action)
        _gameState.value = result.newState

        // Auto-save after every action
        saveGame()
    }

    fun restartGame() {
        val fresh = GameEngine.resetGame()
        _gameState.value = fresh
        saveGame()
    }

    private fun saveGame() {
        viewModelScope.launch {
            repository.saveGame(_gameState.value)
        }
    }

    fun hasSavedGame(): Boolean {
        return if (repository is com.farmsurvival.data.SharedPrefGameRepository) {
            repository.hasSavedGame()
        } else false
    }
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
