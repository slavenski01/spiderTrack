package presentation

import consts.ADDITIONAL_DECKS
import consts.CARDS_ON_SUIT
import consts.CardSuit
import consts.FIELDS_FOR_GAME
import consts.NEED_DECKS_FOR_FINISH
import data.models.Card
import data.models.Deck
import data.models.PlayerStats
import data.repository.DeckRepo
import presentation.models.CompleteDeck
import presentation.models.ForcingFromAdditional
import presentation.models.Moving
import presentation.models.OpenCloseCard
import presentation.models.TransactionTurn
import presentation.models.UserTurn
import presentation.state.MainState
import utils.getCountCloseSlots
import java.util.Stack

class MainViewModel(
    private val deckRepo: DeckRepo
) {
    private var currentGameField = deckRepo.getCurrentDeckState()
    private var state = MainState(gameField = currentGameField)
    private var userTurnStack: Stack<TransactionTurn> = Stack()
    private var countMoves = 0

    init {
        shuffleAndGetDeckState()
    }

    fun getCurrentState() = state

    fun shuffleAndGetDeckState() {

        var allCardsList = createAllCard(currentGameField.suitsInGame).shuffled().toMutableList()

        currentGameField = currentGameField.copy(
            additionalDeck = allCardsList.subList(
                0,
                ADDITIONAL_DECKS * FIELDS_FOR_GAME
            ).toList()
        )

        allCardsList = allCardsList.subList(
            ADDITIONAL_DECKS * FIELDS_FOR_GAME,
            allCardsList.lastIndex + 1
        )

        currentGameField = currentGameField.copy(
            decksInGame = createDecksForGame(allCardsList),
            completableDecksCount = 0
        )
        countMoves = 0
        updateState()
    }

    fun isPossibleStartMove(deckPosition: Int, indexOpenDeckCard: Int): Boolean {
        val currentDeck = currentGameField.decksInGame[deckPosition]
        val openCards = currentDeck.openCards
        val firstCardDragging = openCards[indexOpenDeckCard]

        var tempCard = firstCardDragging
        for (i in (indexOpenDeckCard + 1) until openCards.size) {
            if (
                tempCard.suit != openCards[i].suit
                || (tempCard.value - openCards[i].value) != 1
            ) return false
            tempCard = openCards[i]
        }
        return true
    }

    fun moveCard(
        cardsForMove: List<Card>,
        fromIndexDeck: Int,
        toIndexDeck: Int
    ) {
        val isPossibleMove = isPossibleMoveFromTo(
            cardsForMove = cardsForMove,
            targetDeckIndex = toIndexDeck
        )

        if (isPossibleMove) {
            val userTurns = mutableListOf<UserTurn>()
            addCardsToDeckAndReturn(
                cardsForAdd = cardsForMove,
                targetDeckIndex = toIndexDeck
            )
            userTurns.add(
                Moving(
                    cardsForMove,
                    toIndex = fromIndexDeck,
                    fromIndex = toIndexDeck
                )
            )
            val completeDeckOld = currentGameField.completableDecksCount
            checkCompleteOpenDeckAndUpdate(targetDeckIndex = toIndexDeck)
            val completeDeckNew = currentGameField.completableDecksCount
            if (completeDeckOld < completeDeckNew) {
                userTurns.add(
                    CompleteDeck(
                        toIndexDeck,
                        cardsForMove[0].suit
                    )
                )
            }

            removeCardFromOpen(
                cardForRemove = cardsForMove,
                targetDeckIndex = fromIndexDeck
            )

            val closeToIndexOld = currentGameField.decksInGame[toIndexDeck].closedCards.size
            openOneCloseCardInDeck(toIndexDeck)
            val closeToIndexNew = currentGameField.decksInGame[toIndexDeck].closedCards.size
            if (closeToIndexOld > closeToIndexNew) {
                userTurns.add(OpenCloseCard(toIndexDeck))
            }

            val closeFromIndexOld = currentGameField.decksInGame[fromIndexDeck].closedCards.size
            openOneCloseCardInDeck(fromIndexDeck)
            val closeFromIndexNew = currentGameField.decksInGame[fromIndexDeck].closedCards.size
            if (closeFromIndexOld > closeFromIndexNew) {
                userTurns.add(
                    OpenCloseCard(fromIndexDeck)
                )
            }
            userTurnStack.push(TransactionTurn(userTurns))
            countMoves++
            updateState()
        }
    }

    private fun isPossibleMoveFromTo(
        cardsForMove: List<Card>,
        targetDeckIndex: Int,

        ): Boolean {
        val currentDeck = currentGameField.decksInGame[targetDeckIndex]

        return when {
            currentDeck.openCards.isEmpty() -> {
                true
            }

            currentDeck.openCards.last().value - cardsForMove.first().value == 1 -> {
                true
            }

            else -> false
        }
    }

    private fun addCardsToDeckAndReturn(
        cardsForAdd: List<Card>,
        targetDeckIndex: Int
    ) {
        val currentDeck = currentGameField.decksInGame[targetDeckIndex]
        val openDeck = currentDeck.openCards.toMutableList()

        openDeck.addAll(cardsForAdd)

        val decs = mutableListOf<Deck>()

        for (i in 0 until currentGameField.decksInGame.size) {
            if (i != targetDeckIndex) {
                decs.add(currentGameField.decksInGame[i])
            } else {
                decs.add(
                    currentDeck.copy(
                        openCards = openDeck
                    )
                )
            }
        }

        currentGameField = currentGameField.copy(decksInGame = decs)
    }

    fun forcingAdditionalCardsAndCheckComplete() {
        if (validateForcing()) {
            val decks = mutableListOf<Deck>()
            var tempAdditionalDeck = currentGameField.additionalDeck.toMutableList()
            for (i in 0 until FIELDS_FOR_GAME) {
                val openCards = currentGameField.decksInGame[i].openCards.toMutableList()
                openCards.add(tempAdditionalDeck.last())
                decks.add(
                    currentGameField.decksInGame[i].copy(
                        openCards = openCards
                    )
                )
                tempAdditionalDeck = tempAdditionalDeck.dropLast(1).toMutableList()
            }
            currentGameField = currentGameField.copy(
                decksInGame = decks.toList(),
                additionalDeck = tempAdditionalDeck
            )

            currentGameField.decksInGame.forEachIndexed { index, deck ->
                checkCompleteOpenDeckAndUpdate(targetDeckIndex = index)
            }
            userTurnStack.push(
                TransactionTurn(
                    turns = listOf(ForcingFromAdditional)
                )
            )
            countMoves++
            updateState()
        }
    }

    private fun checkCompleteOpenDeckAndUpdate(
        targetDeckIndex: Int
    ) {
        val currentDeck = currentGameField.decksInGame[targetDeckIndex]
        val openDeck = currentDeck.openCards.toMutableList()
        var countSequence = 0
        var isNeedComplete = false

        if (openDeck.size >= CARDS_ON_SUIT) {
            val openCardReversed = openDeck.reversed()
            var tempCard = openCardReversed[0]
            for (i in 1 until openCardReversed.size) {
                if (openCardReversed[i].value - tempCard.value == 1) {
                    tempCard = openCardReversed[i]
                    countSequence++
                    if (countSequence == CARDS_ON_SUIT - 1) isNeedComplete = true
                } else {
                    break
                }
            }
        }

        val decs = mutableListOf<Deck>()
        for (i in 0 until currentGameField.decksInGame.size) {
            if (i != targetDeckIndex) {
                decs.add(currentGameField.decksInGame[i])
            } else {
                decs.add(
                    currentDeck.copy(
                        openCards = if (isNeedComplete) {
                            openDeck.subList(
                                0,
                                openDeck.size - CARDS_ON_SUIT
                            )
                        } else {
                            openDeck
                        }
                    )
                )
            }
        }

        currentGameField = currentGameField.copy(
            decksInGame = decs,
            completableDecksCount = if (countSequence == CARDS_ON_SUIT - 1) {
                currentGameField.completableDecksCount + 1
            } else {
                currentGameField.completableDecksCount
            }
        )
    }

    private fun openOneCloseCardInDeck(indexDeck: Int) {
        val decksInGame = mutableListOf<Deck>()
        var closedCards = currentGameField.decksInGame[indexDeck].closedCards
        val openCards = currentGameField.decksInGame[indexDeck].openCards.toMutableList()

        if (openCards.size == 0 && closedCards.isNotEmpty()) {
            openCards.add(closedCards.last())
            closedCards = closedCards.subList(0, closedCards.lastIndex)
        }

        val newDeck = Deck(
            positionInGameField = indexDeck,
            closedCards = closedCards,
            openCards = openCards
        )

        for (i in currentGameField.decksInGame.indices) {
            if (i == indexDeck) {
                decksInGame.add(newDeck)
            } else {
                decksInGame.add(currentGameField.decksInGame[i])
            }
        }
        currentGameField = currentGameField.copy(
            decksInGame = decksInGame
        )
    }

    fun cancelTurn() {
        if (userTurnStack.isNotEmpty()) {
            val lastTransaction = userTurnStack.pop()

            lastTransaction.turns.reversed().forEach { turn ->
                when (turn) {
                    is ForcingFromAdditional -> {
                        returnForcingAdditional()
                    }

                    is Moving -> {
                        cancelMove(
                            cardsForMove = turn.cardsForMove,
                            fromIndexDeck = turn.fromIndex,
                            toIndexDeck = turn.toIndex
                        )
                    }

                    is OpenCloseCard -> {
                        cancelOpenCardFromCloseDeck(turn.indexDeck)
                    }

                    is CompleteDeck -> {
                        cancelComplete(indexDeck = turn.indexDeck, cardSuit = turn.suit)
                    }
                }
            }
            countMoves++
            updateState()
        }
    }

    private fun cancelMove(
        cardsForMove: List<Card>,
        fromIndexDeck: Int,
        toIndexDeck: Int
    ) {
        addCardsToDeckAndReturn(
            cardsForAdd = cardsForMove,
            targetDeckIndex = toIndexDeck
        )
        removeCardFromOpen(
            cardForRemove = cardsForMove,
            targetDeckIndex = fromIndexDeck
        )
    }

    private fun cancelOpenCardFromCloseDeck(
        indexDeck: Int
    ) {
        var openCards =
            currentGameField.decksInGame[indexDeck].openCards.toMutableList()
        val closeCards =
            currentGameField.decksInGame[indexDeck].closedCards.toMutableList()

        closeCards.add(openCards.last())
        openCards = openCards.dropLast(1).toMutableList()

        val currentDeck = currentGameField.decksInGame[indexDeck]
        val decs = mutableListOf<Deck>()
        for (i in 0 until currentGameField.decksInGame.size) {
            if (i != indexDeck) {
                decs.add(currentGameField.decksInGame[i])
            } else {
                decs.add(
                    currentDeck.copy(
                        openCards = openCards,
                        closedCards = closeCards
                    )
                )
            }
        }

        currentGameField = currentGameField.copy(decksInGame = decs)
    }

    private fun cancelComplete(
        indexDeck: Int,
        cardSuit: CardSuit
    ) {
        val openCards = currentGameField.decksInGame[indexDeck].openCards.toMutableList()
        val fullSequenceDeck = mutableListOf<Card>()
        repeat(CARDS_ON_SUIT) {
            fullSequenceDeck.add(Card(value = it, suit = cardSuit))
        }
        openCards.addAll(fullSequenceDeck.reversed())
        val currentDeck = currentGameField.decksInGame[indexDeck]
        val decs = mutableListOf<Deck>()
        for (i in 0 until currentGameField.decksInGame.size) {
            if (i != indexDeck) {
                decs.add(currentGameField.decksInGame[i])
            } else {
                decs.add(
                    currentDeck.copy(
                        openCards = openCards,
                    )
                )
            }
        }

        currentGameField = currentGameField.copy(
            decksInGame = decs,
            completableDecksCount = currentGameField.completableDecksCount - 1
        )
    }

    private fun removeCardFromOpen(
        targetDeckIndex: Int,
        cardForRemove: List<Card>
    ) {
        val decksInGame = mutableListOf<Deck>()
        val openCards = currentGameField.decksInGame[targetDeckIndex].openCards
        val currentOpenCards = openCards.subList(
            0,
            openCards.size - cardForRemove.size
        )

        for (i in currentGameField.decksInGame.indices) {
            if (i == targetDeckIndex) {
                decksInGame.add(
                    currentGameField.decksInGame[i].copy(openCards = currentOpenCards)
                )
            } else {
                decksInGame.add(currentGameField.decksInGame[i])
            }
        }
        currentGameField = currentGameField.copy(
            decksInGame = decksInGame
        )
    }

    private fun validateForcing(): Boolean =
        currentGameField.decksInGame.none { it.openCards.isEmpty() } && currentGameField.additionalDeck.isNotEmpty()

    private fun returnForcingAdditional() {
        val currentDeckList = mutableListOf<Deck>()
        val additionalCardsList = currentGameField.additionalDeck.toMutableList()

        for (i in 0 until FIELDS_FOR_GAME) {
            val additionalCard = currentGameField.decksInGame[i].openCards.last()
            additionalCardsList.add(additionalCard)

            val currentDeck = currentGameField.decksInGame[i]
            val currentOpenCards =
                currentDeck.openCards.toMutableList().dropLast(1)
            currentDeckList.add(
                Deck(
                    positionInGameField = i,
                    closedCards = currentDeck.closedCards,
                    openCards = currentOpenCards
                )
            )
        }
        currentGameField = currentGameField.copy(
            decksInGame = currentDeckList,
            additionalDeck = additionalCardsList.toList(),
        )
    }

    private fun createAllCard(
        levelGame: Int
    ): List<Card> {
        val allCardsList = mutableListOf<Card>()
        for (i in 0 until NEED_DECKS_FOR_FINISH) {
            for (j in 0 until CARDS_ON_SUIT) {
                allCardsList.add(
                    Card(
                        value = j,
                        suit = getHelpedSuitList(levelGame)[i],
                    )
                )
            }
        }
        return allCardsList
    }

    private fun createDecksForGame(
        allCardsList: MutableList<Card>
    ): List<Deck> {
        val deckList = mutableListOf<Deck>()
        for (i in 0 until FIELDS_FOR_GAME) {
            var deck = Deck(
                positionInGameField = i,
                closedCards = listOf(),
                openCards = arrayListOf()
            )

            val closeCardsCurrent = mutableListOf<Card>()
            for (j in 0 until deck.getCountCloseSlots()) {
                closeCardsCurrent.add(allCardsList.last())
                allCardsList.removeAt(allCardsList.lastIndex)
            }
            deck = deck.copy(closedCards = closeCardsCurrent)

            deck = deck.copy(openCards = listOf(allCardsList.last()))
            allCardsList.removeAt(allCardsList.lastIndex)

            deckList.add(deck)
        }
        return deckList.toList()
    }

    private fun getHelpedSuitList(
        levelGame: Int
    ): List<CardSuit> {
        val startSuitList = listOf(
            CardSuit.SUIT_CROSS,
            CardSuit.SUIT_DIAMONDS,
            CardSuit.SUIT_HEART,
            CardSuit.SUIT_SPADES
        )
        val indexForChangeSuit = NEED_DECKS_FOR_FINISH / levelGame

        val suitListForGameCards = mutableListOf<CardSuit>()
        var changeIndex = 0
        for (i in 0..NEED_DECKS_FOR_FINISH) {
            suitListForGameCards.add(startSuitList[changeIndex])

            if (i != 0 && i % indexForChangeSuit == 0) {
                changeIndex++
            }
        }
        return suitListForGameCards
    }

    private fun updateState() {
        state = MainState(
            gameField = currentGameField,
            playerStats = PlayerStats(countTurns = countMoves)
        )
    }
}