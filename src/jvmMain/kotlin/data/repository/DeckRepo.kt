package data.repository

import data.models.CurrentGameField

interface DeckRepo {
    fun getCurrentDeckState(): CurrentGameField
    fun updateGameField(gameField: CurrentGameField)
}