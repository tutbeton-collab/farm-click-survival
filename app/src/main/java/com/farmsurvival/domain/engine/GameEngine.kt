package com.farmsurvival.domain.engine

import com.farmsurvival.domain.model.*
import kotlin.random.Random

/**
 * Pure game logic — no Android dependencies.
 * All functions are deterministic given a GameState and GameAction.
 */
object GameEngine {

    private val dailyMessages = listOf(
        "☀️ Прекрасный день для работы на ферме!",
        "🌧️ Дождь — урожай будет хорошим.",
        "🐦 Птицы поют, день обещает быть продуктивным.",
        "🌤️ Облачно, но работать можно.",
        "🦋 Бабочки вокруг — к удачному дню!",
        "💨 Ветрено, будь с осторожностью на поле.",
        "🌈 Радуга после дождя — знак удачи!",
        "🌙 Звёздная ночь, завтра будет ясный день."
    )

    private val randomEvents = listOf(
        RandomEvent(
            name = "Урожайный бонус",
            message = "🎉 Отличная погода! Урожай удвоен!",
            reward = Resources(money = 20, food = 10)
        ),
        RandomEvent(
            name = "Поломка",
            message = "😰 Сломался трактор! Ремонт дорогой.",
            cost = Resources(money = 15, energy = 10)
        ),
        RandomEvent(
            name = "Подарок соседа",
            message = "🎁 Сосед принёс еды!",
            reward = Resources(food = 15)
        ),
        RandomEvent(
            name = "Рыночный бум",
            message = "📈 Цены выросли! Продажи приносят больше.",
            reward = Resources(money = 25)
        ),
        RandomEvent(
            name = "Вредители",
            message = "🐛 Вредители атакуют урожай!",
            cost = Resources(food = 10)
        )
    )

    data class ActionResult(
        val newState: GameState,
        val success: Boolean,
        val message: String,
        val isRandomEvent: Boolean = false
    )

    fun processAction(state: GameState, action: GameAction): ActionResult {
        if (state.isGameOver) {
            return ActionResult(state, false, "Игра окончена!")
        }

        // Check if can afford
        if (!canAfford(state.resources, action.cost)) {
            return ActionResult(
                state.copy(lastActionMessage = "Недостаточно ресурсов для: ${action.displayName}"),
                false,
                "Недостаточно ресурсов!"
            )
        }

        // Apply action
        var newResources = state.resources.minus(action.cost).plus(action.reward).coerceAll()
        var newStats = updateStatistics(state.statistics, action)
        var message = "${action.icon} ${action.displayName}"
        var isRandomEvent = false

        // Check for random event (20% chance)
        if (Random.nextFloat() < 0.2f) {
            val event = randomEvents.random()
            newResources = newResources.plus(event.reward).minus(event.cost).coerceAll()
            message += "\n${event.message}"
            isRandomEvent = true
        }

        // Check if day should advance
        val newTotalActions = newStats.totalActions
        val dayShouldAdvance = newTotalActions % GameState.ACTIONS_PER_DAY == 0 &&
                newTotalActions > state.statistics.totalActions

        var newDay = state.day
        var dailyMessage: String? = null

        if (dayShouldAdvance) {
            newDay = state.day + 1
            dailyMessage = "День $newDay\n${dailyMessages.random()}"

            // Daily upkeep
            newResources = newResources.minus(Resources(food = 5)).coerceAll()
            newResources = newResources.plus(Resources(energy = 10)).coerceAll()
        }

        // Check game over
        val isGameOver = !newResources.isAlive
        val gameOverReason = if (isGameOver) {
            when {
                newResources.energy <= 0 && newResources.food <= 0 -> "Вы выдохлись и голодны..."
                newResources.energy <= 0 -> "Вы слишком устали для работы."
                newResources.food <= 0 -> "Закончилась еда..."
                else -> "Ферма разорена."
            }
        } else null

        val newState = state.copy(
            day = newDay,
            resources = newResources,
            isGameOver = isGameOver,
            gameOverReason = gameOverReason,
            lastActionMessage = message,
            statistics = newStats.copy(bestDay = maxOf(newStats.bestDay, newDay)),
            dailyMessage = dailyMessage,
            timestamp = System.currentTimeMillis()
        )

        return ActionResult(newState, true, message, isRandomEvent)
    }

    fun canAfford(resources: Resources, cost: Resources): Boolean {
        return resources.money >= cost.money &&
                resources.food >= cost.food &&
                resources.energy >= cost.energy
    }

    private fun updateStatistics(stats: GameStatistics, action: GameAction): GameStatistics {
        return when (action) {
            GameAction.HARVEST -> stats.copy(
                totalCropsHarvested = stats.totalCropsHarvested + 1,
                totalEarnings = stats.totalEarnings + action.reward.money,
                totalActions = stats.totalActions + 1
            )
            GameAction.PLANT -> stats.copy(
                totalCropsPlanted = stats.totalCropsPlanted + 1,
                totalActions = stats.totalActions + 1
            )
            GameAction.REST -> stats.copy(
                totalTimesRested = stats.totalTimesRested + 1,
                totalActions = stats.totalActions + 1
            )
            else -> stats.copy(totalActions = stats.totalActions + 1)
        }
    }

    private fun formatReward(reward: Resources): String {
        val parts = mutableListOf<String>()
        if (reward.money > 0) parts.add("💰${reward.money}")
        if (reward.food > 0) parts.add("🍞${reward.food}")
        if (reward.energy > 0) parts.add("⚡${reward.energy}")
        return parts.joinToString(", ")
    }

    fun createInitialState(): GameState = GameState()

    fun resetGame(): GameState = GameState()
}

private data class RandomEvent(
    val name: String,
    val message: String,
    val reward: Resources = Resources(),
    val cost: Resources = Resources()
)
