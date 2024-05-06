package data.repository

import data.models.CurrentGameField
import data.source.Local

class DeckRepository(
    private val localSource: Local
) : DeckRepo {

    override fun getCurrentDeckState(): CurrentGameField = localSource.getCurrentGameField()

    override fun updateGameField(gameField: CurrentGameField) {
        localSource.updateGameField(gameField)
    }
}