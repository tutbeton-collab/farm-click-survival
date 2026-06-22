package com.farmsurvival.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Resources(
    val money: Int = 100,
    val food: Int = 50,
    val energy: Int = 100
) {
    val isAlive: Boolean get() = energy > 0 || food > 0

    fun plus(other: Resources): Resources = Resources(
        money = this.money + other.money,
        food = this.food + other.food,
        energy = this.energy + other.energy
    )

    fun minus(other: Resources): Resources = Resources(
        money = (this.money - other.money).coerceAtLeast(0),
        food = (this.food - other.food).coerceAtLeast(0),
        energy = (this.energy - other.energy).coerceAtLeast(0)
    )

    fun coerceAll(min: Int = 0, max: Int = 9999): Resources = Resources(
        money = this.money.coerceIn(min, max),
        food = this.food.coerceIn(min, max),
        energy = this.energy.coerceIn(min, max)
    )
}

@Serializable
data class GameStatistics(
    val totalCropsHarvested: Int = 0,
    val totalCropsPlanted: Int = 0,
    val totalTimesRested: Int = 0,
    val totalEarnings: Int = 0,
    val bestDay: Int = 1,
    val totalActions: Int = 0
)

@Serializable
data class GameState(
    val day: Int = 1,
    val resources: Resources = Resources(),
    val isGameOver: Boolean = false,
    val gameOverReason: String? = null,
    val lastActionMessage: String? = null,
    val statistics: GameStatistics = GameStatistics(),
    val dailyMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val actionsToday: Int get() = statistics.totalActions % ACTIONS_PER_DAY

    companion object {
        const val ACTIONS_PER_DAY = 5
    }
}

enum class GameAction(
    val displayName: String,
    val icon: String,
    val cost: Resources,
    val reward: Resources
) {
    HARVEST(
        displayName = "Собрать урожай",
        icon = "🌾",
        cost = Resources(energy = 15),
        reward = Resources(money = 30, food = 10)
    ),
    PLANT(
        displayName = "Посадить",
        icon = "🌱",
        cost = Resources(money = 10, energy = 10),
        reward = Resources(food = 20)
    ),
    REPAIR(
        displayName = "Ремонт",
        icon = "🔧",
        cost = Resources(money = 20, energy = 20),
        reward = Resources() // no direct reward, enables bonuses later
    ),
    FEED_ANIMALS(
        displayName = "Кормить животных",
        icon = "🐔",
        cost = Resources(food = 10, energy = 10),
        reward = Resources(money = 25)
    ),
    REST(
        displayName = "Отдохнуть",
        icon = "😴",
        cost = Resources(food = 5),
        reward = Resources(energy = 40)
    );

    companion object {
        val mainActions = listOf(HARVEST, PLANT, REPAIR, FEED_ANIMALS, REST)
    }
}
