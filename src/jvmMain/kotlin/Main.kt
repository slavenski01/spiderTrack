import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.repository.DeckRepository
import data.source.Local
import presentation.MainViewModel

@Composable
@Preview
fun App(

) {
    var text by remember { mutableStateOf("Hello, World!") }
    val mainViewModel = MainViewModel(DeckRepository(Local()))
    mainViewModel.shuffleAndGetDeckState()

    Canvas(modifier = Modifier.fillMaxSize()) {
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}