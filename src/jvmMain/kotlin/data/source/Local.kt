package data.source

import consts.LevelGame
import data.models.CurrentGameField

class Local {

    private var currentGameField = CurrentGameField(
        additionalDeck = listOf(),
        decksInGame = listOf(),
        suitsInGame = LevelGame.SUITS_EASY_LEVEL.getCountSuit(),
        completableDecksCount = 0
    )

    fun getCurrentGameField() = currentGameField

    fun updateGameField(gameField: CurrentGameField) {
        currentGameField = gameField
    }

    fun setLevel(levelGame: LevelGame) {
        currentGameField = currentGameField.copy(
            suitsInGame = levelGame.getCountSuit()
        )
    }
}