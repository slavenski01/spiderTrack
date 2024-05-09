package presentation.ui.draws

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import consts.CARD_HEIGHT
import consts.CARD_WIDTH
import consts.CardSuits
import presentation.ui.model.CardOpenUI
import presentation.ui.model.getColorText
import presentation.ui.model.valueToString
import kotlin.math.min

@Composable
fun CardOpenDraw(
    modifier: Modifier,
    cardOpenUI: CardOpenUI
) {
    Box(
        modifier = modifier
            .background(Color.White)
            .border(width = 2.dp, color = Color.Black)
    ) {
        Column(
            Modifier.fillMaxSize().padding(2.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Canvas(
                    modifier = Modifier
                        .width((CARD_WIDTH / 4).dp)
                        .height((CARD_HEIGHT / 5).dp)
                ) {
                    drawMiniSuit(0f, 0f, size.width, size.height, suit = cardOpenUI.suit)
                }
                Text(
                    text = cardOpenUI.valueToString(),
                    style = TextStyle(color = cardOpenUI.getColorText()),
                    fontSize = 16.sp
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = cardOpenUI.valueToString(),
                style = TextStyle(
                    color = cardOpenUI.getColorText(),
                    textAlign = TextAlign.Center
                ),
                fontSize = 40.sp
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawOpenCard(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    cardOpenUI: CardOpenUI,
    textMeasurer: TextMeasurer
) {
    drawRect(color = Color.White, topLeft = Offset(x, y), alpha = 1f, size = Size(width, height))
    drawCardBorder(x, y, width, height)
    drawMiniSuit(x, y, width, height, cardOpenUI.suit)
    drawValue(x, y, cardOpenUI, textMeasurer, Pair(width, height))
}

fun DrawScope.drawCloseCard(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
) {
    drawCardBorder(x, y, width, height)
    drawRect(
        topLeft = Offset(x, y),
        color = Color.Blue,
        size = Size(width, height),
        style = Fill,
        alpha = 1f
    )
}

fun DrawScope.drawEmptyCard(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
) {
    drawCardBorder(x, y, width, height)
}

private fun DrawScope.drawCardBorder(
    x: Float,
    y: Float,
    width: Float,
    height: Float
) {
    drawRect(
        topLeft = Offset(x, y),
        color = Color.Black,
        size = Size(width, height),
        style = Stroke(width = 4.dp.value)
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawValue(
    topX: Float,
    topY: Float,
    cardOpenUI: CardOpenUI,
    textMeasurer: TextMeasurer,
    cardSize: Pair<Float, Float>
) {
    val fontSize = min(cardSize.first, cardSize.second) * 0.65f
    val fontMiniCenterTop = 15.sp
    val topLeftCenter = Offset(
        x = cardSize.first / 2 - fontSize / 2 + topX,
        y = cardSize.second / 2 - fontSize / 2 + topY
    )
    //topCenter
    drawText(
        textMeasurer = textMeasurer,
        text = cardOpenUI.valueToString(),
        topLeft = Offset(cardSize.first / 2, 10.dp.value),
        maxLines = 1,
        style = TextStyle(
            color = if (cardOpenUI.suit == CardSuits.SUIT_CROSS || cardOpenUI.suit == CardSuits.SUIT_SPADES) {
                Color.Black
            } else {
                Color.Red
            },
            fontSize = fontMiniCenterTop,
        )
    )
    //center
    drawText(
        textMeasurer,
        cardOpenUI.valueToString(),
        topLeft = topLeftCenter,
        maxLines = 1,
        style = TextStyle(
            color = if (cardOpenUI.suit == CardSuits.SUIT_CROSS || cardOpenUI.suit == CardSuits.SUIT_SPADES) {
                Color.Black
            } else {
                Color.Red
            },
            fontSize = fontSize.toSp(),
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

    when (suit) {
        CardSuits.SUIT_CROSS -> {
            drawCrossSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = width.toDp(),
                height = height.toDp()
            )
        }

        CardSuits.SUIT_HEART -> {
            drawHeartSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = width.toDp(),
                height = height.toDp()
            )
        }

        CardSuits.SUIT_DIAMONDS -> {
            drawDiamondsSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = width.toDp(),
                height = height.toDp()
            )
        }

        CardSuits.SUIT_SPADES -> {
            drawSpadesSuit(
                x = x + paddingBorder.value,
                y = y + paddingBorder.value,
                width = width.toDp(),
                height = height.toDp()
            )
        }
    }
}