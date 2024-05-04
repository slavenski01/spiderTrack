package presentation.ui.draws

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import consts.CardSuits
import presentation.ui.model.CardUI

fun DrawScope.drawCard(
    width: Float,
    height: Float,
    cardUI: CardUI
) {
    val paddingBorder = 5.dp

    when (cardUI.suit) {
        CardSuits.SUIT_CROSS -> {

        }

        CardSuits.SUIT_HEART -> {

        }

        CardSuits.SUIT_DIAMONDS -> {

        }

        CardSuits.SUIT_SPADES -> {

        }
    }
}