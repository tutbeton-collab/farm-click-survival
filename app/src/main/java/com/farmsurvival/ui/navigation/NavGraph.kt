package com.farmsurvival.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.farmsurvival.data.GameRepository
import com.farmsurvival.ui.GameViewModel
import com.farmsurvival.ui.GameViewModelFactory
import com.farmsurvival.ui.screens.GameScreen
import com.farmsurvival.ui.screens.StatisticsScreen

sealed class Screen(val route: String) {
    object Game : Screen("game")
    object Statistics : Screen("statistics")
}

@Composable
fun FarmNavHost(repository: GameRepository) {
    val navController = rememberNavController()
    val viewModel: GameViewModel = viewModel(factory = GameViewModelFactory(repository))
    val gameState by viewModel.gameState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Game.route
    ) {
        composable(Screen.Game.route) {
            GameScreen(
                gameState = gameState,
                onAction = { action -> viewModel.performAction(action) },
                onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                onRestart = { viewModel.restartGame() }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                gameState = gameState,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
