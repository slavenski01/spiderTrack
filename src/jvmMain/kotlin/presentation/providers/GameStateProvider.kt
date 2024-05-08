package presentation.providers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.models.CurrentGameField
import data.repository.DeckRepo

class GameStateProvider(deckRepo: DeckRepo) {
    var gameField: CurrentGameField by mutableStateOf(deckRepo.getCurrentDeckState())
}