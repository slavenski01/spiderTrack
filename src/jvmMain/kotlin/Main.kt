import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import consts.CARD_WIDTH
import data.repository.DeckRepository
import data.source.Local
import presentation.MainViewModel
import presentation.providers.GameStateProvider
import presentation.ui.GameField

@Composable
@Preview
fun App(mainViewModel: MainViewModel) {
    Column {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                mainViewModel.shuffleAndGetDeckState()
            }
        ) {
            Text("Новая игра")
        }
        GameField(
            modifier = Modifier.fillMaxWidth(),
            currentGameField = mainViewModel.getCurrentField(),
            onValidateMovement = { indexGameDeck, indexOpenDeck ->
                mainViewModel.isPossibleStartMove(
                    deckPosition = indexGameDeck,
                    deckSubArrayIndex = indexOpenDeck
                )
            },
            onStopMovingOpenCard = { fromIndex, toIndex, cardsArray ->
                mainViewModel.moveCard(
                    targetArrayDeck = cardsArray,
                    fromIndexDeck = fromIndex,
                    toIndexDeck = toIndex
                )
            }
        )
    }
}

fun main() = application {
    Window(
        title = "SpiderTrack",
        onCloseRequest = ::exitApplication,
        resizable = false,
        state = WindowState().apply {
            size = DpSize(
                (CARD_WIDTH * 14).dp,
                700.dp
            )
        }
    ) {
        App(
            MainViewModel(
                GameStateProvider(DeckRepository(Local()))
            )
        )
    }
}