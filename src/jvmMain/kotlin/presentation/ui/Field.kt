package presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import consts.CARD_HEIGHT
import consts.CARD_WIDTH
import consts.DELIMITER_CARD
import consts.MARGIN_CARD
import consts.NEED_DECKS_FOR_FINISH
import data.models.CurrentGameField
import presentation.ui.draws.drawCloseCard
import presentation.ui.draws.drawEmptyCard
import presentation.ui.draws.drawOpenCard

@Composable
fun GameField(
    modifier: Modifier,
    currentGameField: CurrentGameField
) {
    Column(modifier) {
        TopField(Modifier.fillMaxWidth().height(CARD_HEIGHT.dp).padding(MARGIN_CARD.dp))
        BottomField(
            modifier = Modifier.padding(MARGIN_CARD.dp),
            currentGameField = currentGameField
        )
    }
}

@Composable
fun TopField(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AdditionalCard(
            modifier = Modifier
                .width(CARD_WIDTH.dp)
                .height(CARD_HEIGHT.dp)
        )
        Spacer(
            modifier = Modifier
                .width(CARD_WIDTH.dp)
                .height(CARD_HEIGHT.dp)
        )

        for (i in 0 until NEED_DECKS_FOR_FINISH) {
            Canvas(
                Modifier
                    .width(CARD_WIDTH.dp)
                    .height(CARD_HEIGHT.dp)
            ) {
                drawEmptyCard(0f, 0f, size.width, size.height)
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class, ExperimentalFoundationApi::class)
@Composable
fun BottomField(
    modifier: Modifier,
    currentGameField: CurrentGameField,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        currentGameField.decksInGame.forEachIndexed { index, deck ->
            var deckHeightIndex = 0
            deckHeightIndex += deck.closedCards.size
            deck.openCards.forEach { openDeck ->
                deckHeightIndex += openDeck.size
            }
            println(deckHeightIndex)
            Box(
                modifier = modifier
                    .width(CARD_WIDTH.dp)
                    .height((deckHeightIndex * DELIMITER_CARD).dp + CARD_HEIGHT.dp)
            ) {
                var marginIndex = 0
                deck.closedCards.forEachIndexed { index, card ->
                    Canvas(
                        Modifier
                            .width(CARD_WIDTH.dp)
                            .fillMaxHeight()
                            .padding(top = (DELIMITER_CARD * index).dp)
                    ) {
                        drawCloseCard(
                            0f, 0f, size.width, size.height
                        )
                    }
                    marginIndex++
                }

                deck.openCards.forEach { array ->
                    var marginIndexOpenArray = marginIndex
                    array.forEach {
                        val textMeasurer = rememberTextMeasurer()
                        var topBoxOffset by remember { mutableStateOf(Offset(0f, 0f)) }
                        Canvas(
                            Modifier
                                .width(CARD_WIDTH.dp)
                                .fillMaxHeight()
                                .padding(top = (marginIndexOpenArray * DELIMITER_CARD).dp)
                                .offset {
                                    IntOffset(topBoxOffset.x.toInt(), topBoxOffset.y.toInt())
                                }
                                .onDrag(
                                    onDragEnd = { topBoxOffset = Offset(0f, 0f) }
                                ) { // all default: enabled = true, matcher = PointerMatcher.Primary (left mouse button)
                                    topBoxOffset += it
                                }
                        ) {
                            drawOpenCard(
                                x = 0f,
                                y = 0f,
                                width = size.width,
                                height = size.height,
                                cardOpenUI = it.toCardUI(),
                                textMeasurer = textMeasurer
                            )
                        }
                        marginIndexOpenArray++
                    }
                }
            }
        }
    }
}

@Composable
fun AdditionalCard(
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {
        drawCloseCard(
            x = 0f,
            y = 0f,
            width = size.width,
            height = size.height
        )
    }
}