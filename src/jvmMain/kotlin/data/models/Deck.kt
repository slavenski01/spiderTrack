package data.models

import androidx.compose.ui.graphics.Color
import consts.CardSuit

data class Deck(
    val positionInGameField: Int,
    val closedCards: List<Card>,
    val openCards: List<Card>
)

data class Card(
    val value: Int,
    val suit: CardSuit
)

fun Card.valueToString() =
    when (this.value) {
        0 -> "A"
        in 1..9 -> (this.value + 1).toString()
        10 -> "J"
        11 -> "Q"
        12 -> "K"
        else -> "A"
    }

fun Card.getColorText() =
    when (this.suit) {
        CardSuit.SUIT_CROSS, CardSuit.SUIT_SPADES -> Color.Black
        CardSuit.SUIT_HEART, CardSuit.SUIT_DIAMONDS -> Color.Red
    }