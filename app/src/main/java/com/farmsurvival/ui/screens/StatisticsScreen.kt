package com.farmsurvival.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.farmsurvival.domain.model.GameState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    gameState: GameState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Статистика") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Current game stats
            StatsSection(title = "Текущая игра") {
                StatsRow("День", "${gameState.day}")
                StatsRow("Деньги", "💰 ${gameState.resources.money}")
                StatsRow("Еда", "🍞 ${gameState.resources.food}")
                StatsRow("Энергия", "⚡ ${gameState.resources.energy}")
            }

            // Lifetime stats
            StatsSection(title = "Достижения") {
                StatsRow("Собрано урожаев", "${gameState.statistics.totalCropsHarvested}")
                StatsRow("Посажено", "${gameState.statistics.totalCropsPlanted}")
                StatsRow("Отдыхов", "${gameState.statistics.totalTimesRested}")
                StatsRow("Всего заработано", "💰 ${gameState.statistics.totalEarnings}")
                StatsRow("Всего действий", "${gameState.statistics.totalActions}")
                StatsRow("Лучший день", "${gameState.statistics.bestDay}")
            }

            // Status
            StatsSection(title = "Статус") {
                StatsRow(
                    label = "Состояние",
                    value = if (gameState.isGameOver) "💀 Игра окончена" else "✅ В процессе"
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
private fun StatsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 2.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}
