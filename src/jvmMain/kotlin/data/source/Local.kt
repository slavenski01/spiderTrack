package data.source

import consts.SUITS_NORMAL_LEVEL
import data.models.CurrentGameField

class Local() {

    private var currentGameField = CurrentGameField(
        additionalDeck = listOf(),
        decksInGame = listOf(),
        suitsInGame = SUITS_NORMAL_LEVEL,
        completableDecksCount = 0
    )

    fun getCurrentGameField() = currentGameField

    fun updateGameField(gameField: CurrentGameField) {
        currentGameField = gameField
    }
}