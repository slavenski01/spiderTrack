package presentation.ui.model

import consts.CardSuits

data class CardUI(
    val value: Int,
    val suit: CardSuits
)

fun CardUI.valueToString() =
    when (this.value) {
        0 -> "A"
        in 1..9 -> (this.value + 1).toString()
        10 -> "J"
        11 -> "Q"
        12 -> "K"
        else -> "A"
    }
