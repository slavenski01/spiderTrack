import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import consts.CARD_WIDTH
import consts.NEED_DECKS_FOR_FINISH
import data.repository.DeckRepository
import data.source.Local
import presentation.MainViewModel
import presentation.ui.GameField
import presentation.ui.draws.TopHeaderBlock

@Composable
@Preview
fun App(
    mainViewModel: MainViewModel
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        var state by remember { mutableStateOf(mainViewModel.getCurrentState()) }

        TopHeaderBlock(
            countTurns = state.playerStats.countTurns,
            onCancelTurn = {
                mainViewModel.cancelTurn()
                state = mainViewModel.getCurrentState()
            },
            onNewGameClick = {
                mainViewModel.shuffleAndGetDeckState()
                state = mainViewModel.getCurrentState()
            },
            onLevelChangeClick = {
                mainViewModel.setLevelGameFromMenu(positionOptionOnMenu = it)
                state = mainViewModel.getCurrentState()
            }
        )

        GameField(
            modifier = Modifier.fillMaxWidth(),
            currentGameField = state.gameField,
            onClickAdditionalDeck = {
                mainViewModel.forcingAdditionalCardsAndCheckComplete()
                state = mainViewModel.getCurrentState()
            },
            onValidateMovement = { indexGameDeck, indexOpenDeck ->
                mainViewModel.isPossibleStartMove(
                    deckPosition = indexGameDeck,
                    indexOpenDeckCard = indexOpenDeck
                )
            },
            onStopMovingOpenCard = { fromIndex, toIndex, cardsArray ->
                mainViewModel.moveCard(
                    cardsForMove = cardsArray,
                    fromIndexDeck = fromIndex,
                    toIndexDeck = toIndex
                )
                state = mainViewModel.getCurrentState()
            }
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.gameField.completableDecksCount == NEED_DECKS_FOR_FINISH) {
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = "WIN"
                )
            }
        }
    }
}

fun main() = application {
    val windowState = rememberWindowState()
    val mainViewModel = MainViewModel(DeckRepository(Local()))
    Window(
        title = "SpiderTrack",
        onCloseRequest = ::exitApplication,
        resizable = false,
        state = windowState.apply {
            size = DpSize(
                (CARD_WIDTH * 14).dp,
                970.dp
            )
        }
    ) {
        App(mainViewModel)
    }
}