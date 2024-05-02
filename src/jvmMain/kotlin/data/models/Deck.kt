package data.models

data class Deck(
    val positionInGameField: Int,
    val closedCards: List<Int>,
    val openCards: ArrayList<ArrayList<Int>>
)
