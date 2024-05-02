package presentation.models

import data.models.Card

sealed class UserTurn
object ForcingFromAdditional : UserTurn()
data class Moving(val cards: ArrayList<Card>, val fromIndex: Int, val toIndex: Int) : UserTurn()