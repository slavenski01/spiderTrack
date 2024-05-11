package presentation.state

import data.models.CurrentGameField
import data.models.PlayerStats

data class MainState(
    val gameField: CurrentGameField,
    val playerStats: PlayerStats = PlayerStats(0)
)