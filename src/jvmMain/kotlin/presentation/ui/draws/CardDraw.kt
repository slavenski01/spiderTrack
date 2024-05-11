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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import consts.CARD_HEIGHT
import consts.CARD_WIDTH
import consts.CardSuits
import data.models.Card
import data.models.getColorText
import data.models.valueToString

@Composable
fun CardOpenDraw(
    modifier: Modifier,
    card: Card
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
                    drawMiniSuit(0f, 0f, size.width, size.height, suit = card.suit)
                }
                Text(
                    text = card.valueToString(),
                    style = TextStyle(color = card.getColorText()),
                    fontSize = 16.sp
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = card.valueToString(),
                style = TextStyle(
                    color = card.getColorText(),
                    textAlign = TextAlign.Center
                ),
                fontSize = 40.sp
            )
        }
    }
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