package data.repository

import consts.ADDITIONAL_DECKS
import consts.CARDS_ON_SUIT
import consts.FIELDS_FOR_GAME
import consts.NEED_DECKS_FOR_FINISH
import data.models.CurrentGameField
import data.models.Deck
import utils.getCountCloseSlots

class DeckRepository : DeckRepo {

    private var currentGameField = CurrentGameField(
        additionalDeck = listOf(),
        decksInGame = listOf()
    )

    override fun getCurrentDeckState(): CurrentGameField = currentGameField

    override fun shuffleAndGetDeckState(): CurrentGameField {

        var allCardsList = createAllCard().shuffled().toMutableList()

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

    override fun isPossibleMove(deckPosition: Int, deckSubArrayIndex: Int): Boolean {
        return currentGameField.decksInGame[deckPosition].openCards.last().size - 1 == deckSubArrayIndex
    }

    override fun moveCard() {

    }

    private fun createAllCard(): List<Int> {
        val allCardsList = mutableListOf<Int>()
        for (i in 0 until NEED_DECKS_FOR_FINISH) {
            for (j in 0 until CARDS_ON_SUIT) {
                allCardsList.add(j)
            }
        }
        return allCardsList
    }

    private fun createDecksForGame(
        allCardsList: MutableList<Int>
    ): List<Deck> {
        val deckList = mutableListOf<Deck>()
        for (i in 0 until FIELDS_FOR_GAME) {
            var deck = Deck(
                positionInGameField = i,
                closedCards = listOf(),
                openCards = arrayListOf()
            )

            val closeCardsCurrent = mutableListOf<Int>()
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
}