package consts

const val NEED_DECKS_FOR_FINISH = 8
const val ADDITIONAL_DECKS = 5
const val FIELDS_FOR_GAME = 10
const val CARDS_ON_SUIT = 13

enum class LevelGame {
    SUITS_EASY_LEVEL,
    SUITS_NORMAL_LEVEL,
    SUITS_HARD_LEVEL;

    fun getCountSuit() =
        when (this) {
            SUITS_EASY_LEVEL -> 1
            SUITS_NORMAL_LEVEL -> 2
            SUITS_HARD_LEVEL -> 4
        }
}

const val CARD_WIDTH = 100f
const val CARD_HEIGHT = 150f
const val MARGIN_CARD = 20
const val DELIMITER_CARD = 18
