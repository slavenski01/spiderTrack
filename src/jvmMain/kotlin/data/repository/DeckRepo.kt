package data.repository

import consts.LevelGame
import data.models.CurrentGameField

interface DeckRepo {
    fun getCurrentDeckState(): CurrentGameField
    fun updateGameField(gameField: CurrentGameField)

    fun setLevelGame(levelGame: LevelGame)
}