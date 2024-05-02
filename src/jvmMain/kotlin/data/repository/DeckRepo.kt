package data.repository

import data.models.Card
import data.models.CurrentGameField

interface DeckRepo {
    fun getCurrentDeckState(): CurrentGameField
    fun shuffleAndGetDeckState(): CurrentGameField

    fun isPossibleStartMove(deckPosition: Int, deckSubArrayIndex: Int): Boolean
    fun moveCard(targetArrayDeck: ArrayList<Card>, fromIndexDeck: Int, toIndexDeck: Int)
    fun forcingAdditionalCards()

    fun completeDeck(cardsForComplete: ArrayList<Card>)
}