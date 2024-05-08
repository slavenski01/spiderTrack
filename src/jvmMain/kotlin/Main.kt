import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.repository.DeckRepository
import data.source.Local
import presentation.MainViewModel
import presentation.ui.GameField
import presentation.ui.draws.drawField

@OptIn(ExperimentalTextApi::class)
@Composable
@Preview
fun App(

) {
    val mainViewModel = MainViewModel(DeckRepository(Local()))
    val currentGameField = mainViewModel.shuffleAndGetDeckState()
    val decks = mainViewModel.shuffleAndGetDeckState().decksInGame
    var countNeedMeasurers = 0

    decks.forEach {
        it.openCards.forEach { openCards ->
            countNeedMeasurers += openCards.size
        }
    }

    val textMeasurers = mutableListOf<TextMeasurer>()
    for (i in 0..countNeedMeasurers) {
        textMeasurers.add(rememberTextMeasurer())
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawField(
            width = size.width,
            height = size.height,
            topLeftOffset = Offset(1f, 1f),
            currentGameField = currentGameField,
            textMeasurers = textMeasurers,
        )
    }
}

@Composable
@Preview
fun AppV2() {
    val mainViewModel = MainViewModel(DeckRepository(Local()))
    val currentGameField = remember { mainViewModel.shuffleAndGetDeckState() }

    GameField(
        modifier = Modifier.fillMaxSize(),
        currentGameField
    )
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppV2()
    }
}