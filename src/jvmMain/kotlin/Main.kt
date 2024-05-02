import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.repository.DeckRepository
import data.source.Local
import presentation.MainViewModel

@Composable
@Preview
fun App() {
    val viewModel = MainViewModel(deckRepository = DeckRepository(Local()))

    var text by remember { mutableStateOf("Hello, World!") }
    viewModel.shuffleAndGetDeckState()
    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
