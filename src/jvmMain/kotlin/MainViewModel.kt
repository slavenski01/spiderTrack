import consts.*
import data.models.Card
import data.models.CurrentGameField
import data.models.Deck
import data.repository.DeckRepository
import utils.getCountCloseSlots

class MainViewModel(
    private val deckRepository: DeckRepository
) {
    private var currentGameField = deckRepository.getCurrentDeckState()

    fun shuffleAndGetDeckState(): CurrentGameField {

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

        return currentGameField
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

    fun completeDeck(cardsForComplete: ArrayList<Card>) {
        currentGameField = currentGameField.copy(completableDecksCount = currentGameField.completableDecksCount + 1)
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
                currentDeck.openCards.last().addAll(arrayForAdd)
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