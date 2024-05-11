package data.models

import androidx.compose.ui.graphics.Color
import consts.CardSuits

data class Deck(
    val positionInGameField: Int,
    val closedCards: List<Card>,
    val openCards: List<Card>
)

data class Card(
    val value: Int,
    val suit: CardSuits
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
        CardSuits.SUIT_CROSS, CardSuits.SUIT_SPADES -> Color.Black
        CardSuits.SUIT_HEART, CardSuits.SUIT_DIAMONDS -> Color.Red
    }