package com.farmsurvival.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farmsurvival.domain.model.GameAction
import com.farmsurvival.domain.model.GameState
import com.farmsurvival.domain.engine.GameEngine
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    gameState: GameState,
    onAction: (GameAction) -> Unit,
    onNavigateToStats: () -> Unit,
    onRestart: () -> Unit
) {
    var showRestartDialog by remember { mutableStateOf(false) }
    var lastDayMessage by remember { mutableStateOf<String?>(null) }

    // Show day message as toast-like notification
    LaunchedEffect(gameState.day) {
        if (gameState.dailyMessage != null) {
            lastDayMessage = gameState.dailyMessage
            delay(3000)
            lastDayMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top bar
            GameTopBar(
                day = gameState.day,
                onStatsClick = onNavigateToStats,
                onRestartClick = { showRestartDialog = true }
            )

            // Resources
            ResourcesBar(resources = gameState.resources)

            // Content
            if (gameState.isGameOver) {
                GameOverContent(
                    gameState = gameState,
                    onRestart = onRestart,
                    modifier = Modifier.weight(1f)
                )
            } else {
                ActionsContent(
                    gameState = gameState,
                    onAction = onAction,
                    lastMessage = gameState.lastActionMessage,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Day notification overlay (non-blocking)
        lastDayMessage?.let { msg ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = msg,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }

    // Restart dialog
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text("Начать заново?") },
            text = { Text("Весь прогресс будет потерян.") },
            confirmButton = {
                TextButton(onClick = {
                    showRestartDialog = false
                    onRestart()
                }) { Text("Да") }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun GameTopBar(
    day: Int,
    onStatsClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚜 Farm Click Survival",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "День $day",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onStatsClick) {
                    Icon(Icons.Default.BarChart, contentDescription = "Статистика",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                IconButton(onClick = onRestartClick) {
                    Icon(Icons.Default.Refresh, contentDescription = "Рестарт",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
private fun ResourcesBar(resources: com.farmsurvival.domain.model.Resources) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ResourceItem("💰", "Деньги", resources.money, Color(0xFFFFC107))
            ResourceItem("🍞", "Еда", resources.food, Color(0xFF8D6E63))
            ResourceItem("⚡", "Энергия", resources.energy, Color(0xFF2196F3))
        }
    }
}

@Composable
private fun ResourceItem(icon: String, label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 24.sp)
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionsContent(
    gameState: GameState,
    onAction: (GameAction) -> Unit,
    lastMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Выберите действие:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        GameAction.mainActions.forEach { action ->
            val canAfford = GameEngine.canAfford(gameState.resources, action.cost)
            ActionCard(
                action = action,
                enabled = canAfford,
                onClick = { onAction(action) }
            )
        }

        // Last action message
        lastMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = msg,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ActionCard(
    action: GameAction,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = action.icon, fontSize = 32.sp, modifier = Modifier.padding(end = 16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val costParts = mutableListOf<String>()
                if (action.cost.money > 0) costParts.add("💰-${action.cost.money}")
                if (action.cost.food > 0) costParts.add("🍞-${action.cost.food}")
                if (action.cost.energy > 0) costParts.add("⚡-${action.cost.energy}")

                val rewardParts = mutableListOf<String>()
                if (action.reward.money > 0) rewardParts.add("💰+${action.reward.money}")
                if (action.reward.food > 0) rewardParts.add("🍞+${action.reward.food}")
                if (action.reward.energy > 0) rewardParts.add("⚡+${action.reward.energy}")

                if (costParts.isNotEmpty()) {
                    Text(
                        text = "Стоимость: ${costParts.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (rewardParts.isNotEmpty()) {
                    Text(
                        text = "Награда: ${rewardParts.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun GameOverContent(
    gameState: GameState,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "💀", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Игра окончена!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = gameState.gameOverReason ?: "",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Вы продержались", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${gameState.day} дней",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Начать заново", fontSize = 18.sp)
        }
    }
}
