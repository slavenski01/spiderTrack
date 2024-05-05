package presentation.ui.draws

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import consts.CardSuits
import presentation.ui.model.CardUI

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawCard(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    cardUI: CardUI,
    textMeasurer: TextMeasurer
) {
    drawMiniSuit(x, y, width, height, cardUI.suit)
    drawValue(x, y, cardUI, textMeasurer)
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawValue(
    x: Float,
    y: Float,
    cardUI: CardUI,
    textMeasurer: TextMeasurer
) {
    drawText(
        textMeasurer,
        cardUI.value.toString(),
        topLeft = Offset(x, y),
        style = TextStyle(
            color = if (cardUI.suit == CardSuits.SUIT_CROSS || cardUI.suit == CardSuits.SUIT_SPADES) {
                Color.Black
            } else {
                Color.Red
            }
        )
    )
}

private fun DrawScope.drawMiniSuit(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    suit: CardSuits
) {
    val paddingBorder = 5.dp
    val widthSuit = width / 4f
    val heightSuit = height / 6f

    when (suit) {
        CardSuits.SUIT_CROSS -> {
            drawCrossSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
            drawCrossSuit(
                x = x + (width - paddingBorder.value - widthSuit),
                y = y + (height - paddingBorder.value - heightSuit),
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
        }

        CardSuits.SUIT_HEART -> {
            drawHeartSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
            drawHeartSuit(
                x = x + (width - paddingBorder.value - widthSuit),
                y = y + (height - paddingBorder.value - heightSuit),
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
        }

        CardSuits.SUIT_DIAMONDS -> {
            drawDiamondsSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
            drawDiamondsSuit(
                x = x + (width - paddingBorder.value - widthSuit),
                y = y + (height - paddingBorder.value - heightSuit),
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
        }

        CardSuits.SUIT_SPADES -> {
            drawSpadesSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
            drawSpadesSuit(
                x = x + (width - paddingBorder.value - widthSuit),
                y = y + (height - paddingBorder.value - heightSuit),
                width = widthSuit.toDp(),
                height = heightSuit.toDp()
            )
        }
    }
}