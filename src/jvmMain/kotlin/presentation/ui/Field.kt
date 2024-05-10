package presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import consts.CARD_HEIGHT
import consts.CARD_WIDTH
import consts.DELIMITER_CARD
import consts.FIELDS_FOR_GAME
import consts.MARGIN_CARD
import consts.NEED_DECKS_FOR_FINISH
import data.models.Card
import data.models.CurrentGameField
import presentation.ui.draws.CardOpenDraw
import presentation.ui.draws.drawEmptyCard
import utils.bringToFront

@Composable
fun GameField(
    modifier: Modifier,
    currentGameField: CurrentGameField,
    onValidateMovement: (Int, Int) -> Boolean,
    onStopMovingOpenCard: (Int, Int, ArrayList<Card>) -> Unit
) {
    Column(modifier) {
        TopField(Modifier.fillMaxWidth().height(CARD_HEIGHT.dp).padding(MARGIN_CARD.dp))
        BottomField(
            modifier = Modifier.padding(MARGIN_CARD.dp),
            currentGameField = currentGameField,
            onValidateMovement = onValidateMovement,
            onStopMovingOpenCard = onStopMovingOpenCard
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomField(
    modifier: Modifier,
    currentGameField: CurrentGameField,
    onValidateMovement: (Int, Int) -> Boolean,
    //fromDeckIndex, toDeckIndex, cards for movement
    onStopMovingOpenCard: (Int, Int, ArrayList<Card>) -> Unit,
) {
    var indexDraggingDeck by remember { mutableStateOf(0) }
    val decsCords by remember { mutableStateOf(mutableListOf<Offset>()) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        currentGameField.decksInGame.forEachIndexed { indexDeck, deck ->
            var deckHeightIndex = 0
            deckHeightIndex += deck.closedCards.size
            deckHeightIndex += deck.openCards.size

            Box(
                modifier = modifier
                    .width(CARD_WIDTH.dp)
                    .height((deckHeightIndex * DELIMITER_CARD).dp + CARD_HEIGHT.dp)
                    .let {
                        if (indexDraggingDeck == indexDeck) it.bringToFront() else it
                    }
                    .onGloballyPositioned { position ->
                        val x = position.positionInWindow().x
                        val y = position.positionInWindow().y
                        if (decsCords.size < FIELDS_FOR_GAME) {
                            decsCords.add(Offset(x, y))
                        }
                    }
            ) {
                var marginIndex = 0
                deck.closedCards.forEachIndexed { index, card ->
                    Box(
                        Modifier
                            .width(CARD_WIDTH.dp)
                            .fillMaxHeight()
                            .padding(top = (DELIMITER_CARD * index).dp)
                            .border(width = 2.dp, color = Color.Black)
                            .background(Color.Blue)
                    )
                    marginIndex++
                }

                var marginIndexOpenArray = marginIndex
                deck.openCards.forEachIndexed { indexOpenDeck, card ->
                    var topBoxOffset by remember { mutableStateOf(Offset(0f, 0f)) }
                    Box(
                        modifier = Modifier
                            .width(CARD_WIDTH.dp)
                            .fillMaxHeight()
                            .padding(top = (marginIndexOpenArray * DELIMITER_CARD).dp)
                            .offset {
                                IntOffset(topBoxOffset.x.toInt(), topBoxOffset.y.toInt())
                            }
                            .onDrag(
                                onDragEnd = {
                                    val cordsToX = decsCords[indexDeck].x + topBoxOffset.x
                                    var indexToDeck = 0

                                    if (cordsToX > decsCords.last().x) {
                                        indexToDeck = decsCords.lastIndex
                                    } else {
                                        for (i in 0..decsCords.size - 2) {
                                            if (cordsToX in decsCords[i].x..decsCords[i + 1].x) {
                                                indexToDeck = i
                                            }
                                        }
                                    }

                                    topBoxOffset = Offset(0f, 0f)
                                    onStopMovingOpenCard(
                                        indexDeck,
                                        indexToDeck,
                                        arrayListOf(card)
                                    )
                                },
                                onDrag = {
                                    if (onValidateMovement(indexDeck, indexOpenDeck)) {
                                        indexDraggingDeck = indexDeck
                                        topBoxOffset += it
                                    }
                                }
                            )
                    ) {
                        CardOpenDraw(
                            modifier = Modifier.fillMaxSize(),
                            cardOpenUI = card.toCardUI()
                        )
                    }
                    marginIndexOpenArray++
                }
            }
        }
    }
}

@Composable
fun AdditionalCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .width(CARD_WIDTH.dp)
            .fillMaxHeight()
            .border(width = 2.dp, color = Color.Black)
            .background(Color.Blue)
    )
}