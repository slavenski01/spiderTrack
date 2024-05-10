import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import consts.CARD_WIDTH
import data.repository.DeckRepository
import data.source.Local
import presentation.MainViewModel
import presentation.ui.GameField

@Composable
@Preview
fun App(mainViewModel: MainViewModel) {
    Column {
        var state by mutableStateOf(mainViewModel.getCurrentState())

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                mainViewModel.shuffleAndGetDeckState()
                state = mainViewModel.getCurrentState()
            }
        ) {
            Text("Новая игра")
        }
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
    }
}

fun main() = application {
    val windowState = rememberWindowState()
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
        val mainViewModel = MainViewModel(DeckRepository(Local()))
        App(
            mainViewModel
        )
    }
}