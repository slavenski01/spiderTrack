package presentation.ui

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
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import consts.CARD_HEIGHT
import consts.CARD_WIDTH
import consts.DELIMITER_CARD
import consts.FIELDS_FOR_GAME
import consts.MARGIN_CARD
import consts.NEED_DECKS_FOR_FINISH
import data.models.Card
import data.models.CurrentGameField
import presentation.ui.draws.CardOpenDraw
import utils.bringToFront

@Composable
fun GameField(
    modifier: Modifier,
    currentGameField: CurrentGameField,
    onClickAdditionalDeck: () -> Unit,
    onValidateMovement: (Int, Int) -> Boolean,
    onStopMovingOpenCard: (Int, Int, List<Card>) -> Unit
) {
    Column(modifier) {
        TopField(
            modifier = Modifier.fillMaxWidth().height(CARD_HEIGHT.dp).padding(MARGIN_CARD.dp),
            countComplete = currentGameField.completableDecksCount,
            countAdditional = currentGameField.additionalDeck.size / FIELDS_FOR_GAME,
            onClickAdditionalDeck = onClickAdditionalDeck
        )
        BottomField(
            modifier = Modifier.padding(MARGIN_CARD.dp),
            currentGameField = currentGameField,
            onValidateMovement = onValidateMovement,
            onStopMovingOpenCard = onStopMovingOpenCard
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopField(
    modifier: Modifier = Modifier,
    countComplete: Int,
    countAdditional: Int,
    onClickAdditionalDeck: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (countAdditional > 0) {
            AdditionalCard(
                modifier = Modifier
                    .width(CARD_WIDTH.dp)
                    .height(CARD_HEIGHT.dp)
                    .onClick { onClickAdditionalDeck() },
                countAdditional = countAdditional
            )
        }

        Spacer(
            modifier = Modifier
                .width(CARD_WIDTH.dp)
                .height(CARD_HEIGHT.dp)
        )

        for (i in 0 until countComplete) {
            Box(
                Modifier
                    .width(CARD_WIDTH.dp)
                    .fillMaxHeight()
                    .padding(top = (DELIMITER_CARD).dp)
                    .border(width = 2.dp, color = Color.Black)
                    .background(Color.Blue)
            )
        }
        for (i in 0 until NEED_DECKS_FOR_FINISH - countComplete) {
            Box(
                Modifier
                    .width(CARD_WIDTH.dp)
                    .fillMaxHeight()
                    .padding(top = (DELIMITER_CARD).dp)
                    .border(width = 2.dp, color = Color.Black)
                    .background(Color.White)
            )
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
    onStopMovingOpenCard: (Int, Int, List<Card>) -> Unit,
) {
    var indexDraggingDeck by remember { mutableStateOf(0) }
    val dekcInGamePositionCords by remember { mutableStateOf(mutableListOf<Offset>()) }

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
                        if (dekcInGamePositionCords.size < FIELDS_FOR_GAME) {
                            dekcInGamePositionCords.add(Offset(x, y))
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
                var topBoxOffset by remember { mutableStateOf(Pair(0, Offset(0f, 0f))) }
                deck.openCards.forEachIndexed { indexOpenDeckCard, card ->
                    Box(
                        modifier = Modifier
                            .width(CARD_WIDTH.dp)
                            .fillMaxHeight()
                            .padding(top = (marginIndexOpenArray * DELIMITER_CARD).dp)
                            .offset {
                                if (topBoxOffset.first <= indexOpenDeckCard) {
                                    IntOffset(
                                        topBoxOffset.second.x.toInt(),
                                        topBoxOffset.second.y.toInt()
                                    )
                                } else {
                                    IntOffset(0, 0)
                                }
                            }
                            .onDrag(
                                onDrag = {
                                    if (onValidateMovement(indexDeck, indexOpenDeckCard)) {
                                        indexDraggingDeck = indexDeck
                                        topBoxOffset = Pair(
                                            indexOpenDeckCard,
                                            Offset(
                                                it.x + topBoxOffset.second.x,
                                                it.y + topBoxOffset.second.y
                                            )
                                        )
                                    }
                                },
                                onDragEnd = {
                                    val cordsToX =
                                        dekcInGamePositionCords[indexDeck].x + topBoxOffset.second.x
                                    var indexToDeck = 0

                                    if (cordsToX > dekcInGamePositionCords.last().x) {
                                        indexToDeck = dekcInGamePositionCords.lastIndex
                                    } else {
                                        for (i in 0..dekcInGamePositionCords.size - 2) {
                                            if (cordsToX in dekcInGamePositionCords[i].x..dekcInGamePositionCords[i + 1].x) {
                                                indexToDeck = i
                                            }
                                        }
                                    }
                                    val targetListForMove = mutableListOf<Card>()
                                    for (i in indexOpenDeckCard until deck.openCards.size) {
                                        targetListForMove.add(deck.openCards[i])
                                    }

                                    onStopMovingOpenCard(
                                        indexDeck,
                                        indexToDeck,
                                        targetListForMove.toList()
                                    )
                                    targetListForMove.clear()
                                    topBoxOffset = Pair(0, Offset(0f, 0f))
                                }
                            )
                    ) {
                        CardOpenDraw(
                            modifier = Modifier.fillMaxSize(),
                            card = card
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
    modifier: Modifier = Modifier,
    countAdditional: Int
) {
    Box(
        modifier
            .width(CARD_WIDTH.dp)
            .fillMaxHeight()
            .border(width = 2.dp, color = Color.Black)
            .background(Color.Blue)
    ) {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = countAdditional.toString(),
            fontSize = 20.sp,
            style = TextStyle(textAlign = TextAlign.Center),
        )
    }
}