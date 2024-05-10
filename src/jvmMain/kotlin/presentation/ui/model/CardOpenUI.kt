package presentation.ui.model

import androidx.compose.ui.graphics.Color
import consts.CardSuits

data class CardOpenUI(
    val value: Int,
    val suit: CardSuits
)

fun CardOpenUI.valueToString() =
    when (this.value) {
        0 -> "A"
        in 1..9 -> (this.value + 1).toString()
        10 -> "J"
        11 -> "Q"
        12 -> "K"
        else -> "A"
    }

fun CardOpenUI.getColorText() =
    when (this.suit) {
        CardSuits.SUIT_CROSS, CardSuits.SUIT_SPADES -> Color.Black
        CardSuits.SUIT_HEART, CardSuits.SUIT_DIAMONDS -> Color.Red
    }
