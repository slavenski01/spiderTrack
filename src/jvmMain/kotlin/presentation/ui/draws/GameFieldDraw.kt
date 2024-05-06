package presentation.ui.draws

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.dp
import consts.CARD_HEIGHT
import consts.DELIMITER_CARD
import consts.FIELDS_FOR_GAME
import consts.MARGIN_CARD
import consts.NEED_DECKS_FOR_FINISH
import data.models.CurrentGameField
import data.models.Deck

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawField(
    width: Float,
    height: Float,
    topLeftOffset: Offset,
    currentGameField: CurrentGameField,
    textMeasurers: List<TextMeasurer>
) {
    val marginCard = MARGIN_CARD.dp.value
    val widthCard = width / (FIELDS_FOR_GAME + 2)

    drawAdditionalDeck(
        width = widthCard,
        height = CARD_HEIGHT,
        topLeftOffset = topLeftOffset.copy(
            x = topLeftOffset.x + marginCard,
            y = topLeftOffset.y + marginCard
        )
    )

    drawEmptyDeckOnTop(
        width = widthCard,
        height = CARD_HEIGHT,
        topLeftOffset = topLeftOffset.copy(
            x = topLeftOffset.x + marginCard + widthCard * 2,
            y = topLeftOffset.y + marginCard
        ),
        completeDeckCount = currentGameField.completableDecksCount
    )

    drawDecksInGame(
        width = widthCard,
        height = CARD_HEIGHT,
        topLeftOffset = topLeftOffset.copy(
            x = topLeftOffset.x + marginCard,
            y = topLeftOffset.y + marginCard * 2 + CARD_HEIGHT
        ),
        decks = currentGameField.decksInGame,
        textMeasurers = textMeasurers
    )
}

fun DrawScope.drawAdditionalDeck(
    width: Float,
    height: Float,
    topLeftOffset: Offset,
) {
    drawCloseCard(
        x = topLeftOffset.x,
        y = topLeftOffset.y,
        width = width,
        height = height
    )
}

fun DrawScope.drawEmptyDeckOnTop(
    width: Float,
    height: Float,
    topLeftOffset: Offset,
    completeDeckCount: Int
) {
    val marginCard = MARGIN_CARD.dp
    for (i in 0 until NEED_DECKS_FOR_FINISH) {
        if (i < completeDeckCount) {
            drawCloseCard(
                x = topLeftOffset.x + width * i + marginCard.value,
                y = topLeftOffset.y,
                width = width,
                height = height
            )
        } else {
            drawEmptyCard(
                x = topLeftOffset.x + width * i + marginCard.value,
                y = topLeftOffset.y,
                width = width,
                height = height
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawDecksInGame(
    width: Float,
    height: Float,
    topLeftOffset: Offset,
    decks: List<Deck>,
    textMeasurers: List<TextMeasurer>
) {
    var currentIndexMeasurer = 0
    val marginCard = MARGIN_CARD.dp
    val delimiterCard = DELIMITER_CARD.dp

    decks.forEachIndexed { indexX, deck ->
        deck.closedCards.forEachIndexed { index, card ->
            drawCloseCard(
                x = topLeftOffset.x + width * indexX + marginCard.value,
                y = topLeftOffset.y + delimiterCard.value * index,
                width = width,
                height = height
            )
        }
        var lastYOpenCard = 0f
        deck.openCards.forEachIndexed { indexOpen, openCards ->
            val closeCardsHeight = deck.closedCards.size * delimiterCard.value
            lastYOpenCard = closeCardsHeight * indexOpen
            openCards.forEachIndexed { index, card ->
                drawOpenCard(
                    x = topLeftOffset.x + width * indexX + marginCard.value,
                    y = topLeftOffset.y + delimiterCard.value * index + closeCardsHeight,
                    width = width,
                    height = height,
                    cardOpenUI = card.toCardUI(),
                    textMeasurer = textMeasurers[currentIndexMeasurer],
                )
                currentIndexMeasurer++
            }
        }
    }
}