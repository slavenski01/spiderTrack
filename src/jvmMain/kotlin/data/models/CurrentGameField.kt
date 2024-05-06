package data.models

data class CurrentGameField(
    val additionalDeck: List<Card>,
    val decksInGame: List<Deck>,
    val suitsInGame: Int,
    val completableDecksCount: Int
)
