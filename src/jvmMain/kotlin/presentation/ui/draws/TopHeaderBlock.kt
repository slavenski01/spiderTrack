package presentation.ui.draws

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColumnScope.TopHeaderBlock(
    countTurns: Int,
    onCancelTurn: () -> Unit,
    onNewGameClick: () -> Unit,
    onLevelChangeClick: (Int) -> Unit
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Ходов: $countTurns",
        style = TextStyle(textAlign = TextAlign.Center),
        fontSize = 20.sp
    )
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        Button(
            onClick = { onCancelTurn() }
        ) {
            Text("Отменить ход")
        }
        Button(
            modifier = Modifier.padding(horizontal = 20.dp),
            onClick = { onNewGameClick() }
        ) {
            Text("Новая игра")
        }
        Box {
            var expanded by remember { mutableStateOf(false) }
            Button(
                onClick = { expanded = true }
            ) {
                Text(text = "Выбрать уровень")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = { onLevelChangeClick(0) },
                ) {
                    Text("1 Масть")
                }
                DropdownMenuItem(
                    onClick = { onLevelChangeClick(1) },
                ) {
                    Text("2 Масти")
                }
                DropdownMenuItem(
                    onClick = { onLevelChangeClick(2) }
                ) {
                    Text("3 Масти")
                }
            }
        }
    }
}