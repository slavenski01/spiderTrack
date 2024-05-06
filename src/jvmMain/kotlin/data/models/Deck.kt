package data.models

import consts.CardSuits
import presentation.ui.model.CardOpenUI

data class Deck(
    val positionInGameField: Int,
    val closedCards: List<Card>,
    val openCards: ArrayList<ArrayList<Card>>
)

data class Card(
    val value: Int,
    val suit: CardSuits
) {
    fun toCardUI() = CardOpenUI(value, suit)
}
