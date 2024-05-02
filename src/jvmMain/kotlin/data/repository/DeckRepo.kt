package data.repository

import data.models.CurrentGameField

interface DeckRepo {
    fun getCurrentDeckState(): CurrentGameField
    fun shuffleAndGetDeckState(): CurrentGameField

    fun isPossibleMove(deckPosition: Int, deckSubArrayIndex: Int): Boolean
    fun moveCard()
}