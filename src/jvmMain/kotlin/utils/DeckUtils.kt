package utils

import data.models.Deck

fun Deck.getCountCloseSlots(): Int {
    return when (this.positionInGameField) {
        in 0..3 -> 5
        else -> 4
    }
}