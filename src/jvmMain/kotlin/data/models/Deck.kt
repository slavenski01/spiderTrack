package data.models

import consts.CardSuits

data class Deck(
    val positionInGameField: Int,
    val closedCards: List<Card>,
    val openCards: ArrayList<ArrayList<Card>>
)

data class Card(
    val value: Int,
    val suit: CardSuits
)
