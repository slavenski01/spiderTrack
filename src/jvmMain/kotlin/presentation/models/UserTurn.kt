package presentation.models

import consts.CardSuit
import data.models.Card

sealed class UserTurn
object ForcingFromAdditional : UserTurn()

data class Moving(
    val cardsForMove: List<Card>,
    val fromIndex: Int,
    val toIndex: Int,
) : UserTurn()

data class OpenCloseCard(val indexDeck: Int) : UserTurn()

data class CompleteDeck(val indexDeck: Int, val suit: CardSuit) : UserTurn()

data class TransactionTurn(
    val turns: List<UserTurn>
)