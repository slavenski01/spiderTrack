package presentation

import consts.ADDITIONAL_DECKS
import consts.CARDS_ON_SUIT
import consts.CardSuits
import consts.FIELDS_FOR_GAME
import consts.NEED_DECKS_FOR_FINISH
import data.models.Card
import data.models.Deck
import data.repository.DeckRepo
import presentation.models.ForcingFromAdditional
import presentation.models.Moving
import presentation.models.UserTurn
import presentation.providers.MainState
import utils.getCountCloseSlots
import java.util.Stack

class MainViewModel(
    private val deckRepo: DeckRepo
) {
    private var currentGameField = deckRepo.getCurrentDeckState()

    private var state = MainState(
        gameField = currentGameField
    )

    init {
        shuffleAndGetDeckState()
    }

    private var userTurnStack: Stack<UserTurn>? = null

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
            decksInGame = createDecksForGame(allCardsList)
        )
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
            addCardsToDeckAndReturn(
                cardsForAdd = cardsForMove,
                targetDeckIndex = toIndexDeck
            )
            checkCompleteOpenDeckAndUpdate(targetDeckIndex = toIndexDeck)
            removeCardFromOpen(
                cardForRemove = cardsForMove,
                targetDeckIndex = fromIndexDeck
            )
            openOneCloseCardInDeck(fromIndexDeck)
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

        currentGameField = currentGameField.copy(
            decksInGame = decs
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

    fun forcingAdditionalCardsAndCheckComplete() {
        if (validateForcing()) {
            val decks = mutableListOf<Deck>()
            val tempAdditionalDeck = currentGameField.additionalDeck.toMutableList()
            for (i in 0 until FIELDS_FOR_GAME) {
                val openCards = currentGameField.decksInGame[i].openCards.toMutableList()
                openCards.add(tempAdditionalDeck.last())
                decks.add(
                    currentGameField.decksInGame[i].copy(
                        openCards = openCards
                    )
                )
                tempAdditionalDeck.removeLast()
            }
            currentGameField = currentGameField.copy(
                decksInGame = decks.toList(),
                additionalDeck = tempAdditionalDeck.toList()
            )

            currentGameField.decksInGame.forEachIndexed { index, deck ->
                checkCompleteOpenDeckAndUpdate(targetDeckIndex = index)
            }
            updateState()
        }
    }

    private fun validateForcing(): Boolean =
        currentGameField.decksInGame.none { it.openCards.isEmpty() }

    fun cancelTurn() {
        userTurnStack?.let {
            when (val turn = it.pop()) {
                is ForcingFromAdditional -> {
                    returnForcingAdditional()
                }

                is Moving -> {
                    moveCard(
                        cardsForMove = turn.cards,
                        fromIndexDeck = turn.toIndex,
                        toIndexDeck = turn.fromIndex
                    )
                }
            }
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
        println(isNeedComplete)
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
                currentGameField.completableDecksCount
            } else {
                currentGameField.completableDecksCount + 1
            }
        )
    }

    private fun returnForcingAdditional() {
        val currentDeckList = mutableListOf<Deck>()
        for (i in 0 until FIELDS_FOR_GAME) {
            val additionalCard = currentGameField.decksInGame[i].openCards.last()
            val additionalCardsList = currentGameField.additionalDeck.toMutableList()
            additionalCardsList.add(additionalCard)

            currentGameField = currentGameField.copy(
                additionalDeck = additionalCardsList.toList(),
            )

            val currentDeck = currentGameField.decksInGame[i]
            currentDeckList.add(
                Deck(
                    positionInGameField = i,
                    closedCards = currentDeck.closedCards,
                    openCards = removeLastCardAndReturnOpenCardsArray(openCards = currentDeck.openCards)
                )
            )
        }
    }

    private fun removeLastCardAndReturnOpenCardsArray(
        openCards: List<Card>
    ): List<Card> {
        val currentOpenCard = arrayListOf<Card>()
        for (i in openCards.indices) {
            if (i == openCards.size - 1) {
                for (j in 0..openCards.size - 2) {
                    currentOpenCard.add(openCards[i])
                }
            } else {
                currentOpenCard.add(openCards[i])
            }
        }
        return currentOpenCard
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
    ): List<CardSuits> {
        val startSuitList = listOf(
            CardSuits.SUIT_CROSS,
            CardSuits.SUIT_DIAMONDS,
            CardSuits.SUIT_HEART,
            CardSuits.SUIT_SPADES
        )
        val indexForChangeSuit = NEED_DECKS_FOR_FINISH / levelGame

        val suitListForGameCards = mutableListOf<CardSuits>()
        var changeIndex = 0
        for (i in 0..NEED_DECKS_FOR_FINISH) {
            suitListForGameCards.add(startSuitList[changeIndex])

            if (i != 0 && i % indexForChangeSuit == 0) {
                changeIndex++
            }
        }
        return suitListForGameCards
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

    private fun updateState() {
        state = MainState(gameField = currentGameField)
    }
}