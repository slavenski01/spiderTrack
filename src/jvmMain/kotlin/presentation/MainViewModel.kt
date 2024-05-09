package presentation

import consts.ADDITIONAL_DECKS
import consts.CARDS_ON_SUIT
import consts.CardSuits
import consts.FIELDS_FOR_GAME
import consts.NEED_DECKS_FOR_FINISH
import consts.SUITS_NORMAL_LEVEL
import data.models.Card
import data.models.CurrentGameField
import data.models.Deck
import presentation.models.ForcingFromAdditional
import presentation.models.Moving
import presentation.models.UserTurn
import presentation.providers.GameStateProvider
import utils.getCountCloseSlots
import java.util.Stack

class MainViewModel(
    stateProvider: GameStateProvider
) {
    private var currentGameField = CurrentGameField(
        listOf(),
        listOf(),
        SUITS_NORMAL_LEVEL,
        0
    )

    private var userTurnStack: Stack<UserTurn>? = null

    private var gameStateProvider = stateProvider

    init {
        shuffleAndGetDeckState()
    }

    fun getCurrentField() = gameStateProvider.gameField

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

        gameStateProvider.gameField = currentGameField
    }

    fun isPossibleStartMove(deckPosition: Int, deckSubArrayIndex: Int): Boolean {
        return currentGameField.decksInGame[deckPosition].openCards.last().size - 1 == deckSubArrayIndex
    }

    fun moveCard(
        targetArrayDeck: ArrayList<Card>,
        fromIndexDeck: Int,
        toIndexDeck: Int
    ) {
        val deckAfterMoveToIndex = addCardsToDeckAndReturn(
            arrayForAdd = targetArrayDeck,
            targetDeckIndex = toIndexDeck,
            isFromAdditional = false
        )
        val deckAfterOpenCard = openOneCloseCardInDeck(fromIndexDeck)

        val gameFieldDecks = mutableListOf<Deck>()
        currentGameField.decksInGame.forEach { deckInGame ->
            when (deckInGame.positionInGameField) {
                deckAfterMoveToIndex.positionInGameField -> {
                    gameFieldDecks.add(deckAfterMoveToIndex)
                }

                deckAfterOpenCard.positionInGameField -> {
                    gameFieldDecks.add(deckAfterOpenCard)
                }

                else -> {
                    gameFieldDecks.add(deckInGame)
                }
            }
        }
        currentGameField = currentGameField.copy(
            decksInGame = gameFieldDecks.toList()
        )
        gameStateProvider.gameField = currentGameField
    }

    fun forcingAdditionalCards() {
        for (i in 0 until FIELDS_FOR_GAME) {
            val additionalCard = currentGameField.additionalDeck.last()
            addCardsToDeckAndReturn(
                arrayForAdd = arrayListOf(additionalCard),
                targetDeckIndex = i,
                isFromAdditional = true
            )
            currentGameField = currentGameField.copy(
                additionalDeck = currentGameField.additionalDeck.subList(
                    0,
                    currentGameField.additionalDeck.lastIndex - 1
                )
            )
        }
    }

    fun cancelTurn() {
        userTurnStack?.let {
            when (val turn = it.pop()) {
                is ForcingFromAdditional -> {
                    returnForcingAdditional()
                }

                is Moving -> {
                    moveCard(
                        targetArrayDeck = turn.cards,
                        fromIndexDeck = turn.toIndex,
                        toIndexDeck = turn.fromIndex
                    )
                }
            }
        }
    }

    private fun completeDeck(cardsForComplete: ArrayList<Card>) {
        currentGameField =
            currentGameField.copy(completableDecksCount = currentGameField.completableDecksCount + 1)
    }

    private fun returnForcingAdditional() {
        val currentDeckList = mutableListOf<Deck>()
        for (i in 0 until FIELDS_FOR_GAME) {
            val additionalCard = currentGameField.decksInGame[i].openCards.last().last()
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
        openCards: ArrayList<ArrayList<Card>>
    ): ArrayList<ArrayList<Card>> {
        val currentOpenCard = arrayListOf(arrayListOf<Card>())
        if (openCards.last().size == 1) {
            for (i in 0 until openCards.size - 1) {
                currentOpenCard.add(openCards[i])
            }
        } else {
            for (i in 0 until openCards.size) {
                if (i == openCards.size - 1) {
                    for (j in 0..openCards[i].size - 2) {
                        currentOpenCard[i].add(openCards[i][j])
                    }
                } else {
                    currentOpenCard.add(openCards[i])
                }
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

            deck = deck.copy(openCards = arrayListOf(arrayListOf(allCardsList.last())))
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

    private fun addCardsToDeckAndReturn(
        arrayForAdd: ArrayList<Card>,
        targetDeckIndex: Int,
        isFromAdditional: Boolean
    ): Deck {
        val currentDeck = currentGameField.decksInGame[targetDeckIndex]

        when {
            currentDeck.openCards.isEmpty() || isFromAdditional -> {
                currentDeck.openCards.add(arrayForAdd)
            }

            currentDeck.openCards.last().last().value - arrayForAdd.first().value == 1 -> {
                if (currentDeck.openCards.last().last().suit == arrayForAdd.first().suit) {
                    currentDeck.openCards.last().addAll(arrayForAdd)
                } else {
                    currentDeck.openCards.add(arrayForAdd)
                }
            }
        }

        return currentDeck
    }

    private fun openOneCloseCardInDeck(indexDeck: Int): Deck {
        var closedCards = currentGameField.decksInGame[indexDeck].closedCards
        val openCards = currentGameField.decksInGame[indexDeck].openCards

        if (openCards.size == 0 && closedCards.isNotEmpty()) {
            openCards.add(arrayListOf(closedCards.last()))
            closedCards = closedCards.subList(0, closedCards.lastIndex - 1)
        }

        return Deck(
            positionInGameField = indexDeck,
            closedCards = closedCards,
            openCards = openCards
        )
    }
}