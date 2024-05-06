import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import consts.CardSuits
import data.repository.DeckRepository
import data.source.Local
import presentation.MainViewModel
import presentation.ui.draws.drawCard
import presentation.ui.model.CardUI

@OptIn(ExperimentalTextApi::class)
@Composable
@Preview
fun App(

) {
    val mainViewModel = MainViewModel(DeckRepository(Local()))
    mainViewModel.shuffleAndGetDeckState()

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCard(
            x = 200f,
            y = 200f,
            width = size.width / 12f,
            height = size.height / 6f,
            cardUI = CardUI(0, CardSuits.SUIT_CROSS),
            textMeasurer
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}