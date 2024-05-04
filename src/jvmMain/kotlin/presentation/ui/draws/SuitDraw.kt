package presentation.ui.draws

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun DrawScope.drawDiamondsSuit(
    x: Float,
    y: Float,
    width: Dp,
    height: Dp
) {
    val path = Path().apply {
        moveTo((x + width.value) / 2f, y)
        lineTo(x + width.value, (y + height.value) / 2f)
        lineTo((x + width.value) / 2f, y + height.value)
        lineTo(x, height.value / 2f)
        lineTo((x + width.value) / 2f, y)
        close()
    }
    drawPath(path, color = Color.Red)
}

fun DrawScope.drawHeartSuit(
    x: Float,
    y: Float,
    width: Dp,
    height: Dp
) {
    val path = Path().apply {
        moveTo(x, y)
        lineTo(x + width.value, y)
        lineTo((x + width.value) / 2f, y + height.value)
        lineTo(x, y)
        close()
    }
    drawPath(path, color = Color.Red)
}

fun DrawScope.drawCrossSuit(
    x: Float,
    y: Float,
    width: Dp,
    height: Dp
) {
    drawLine(
        start = Offset((x + width.value) / 2f, y),
        end = Offset((x + width.value) / 2f, y + height.value),
        color = Color.Black,
        strokeWidth = 20.dp.value
    )
    drawLine(
        start = Offset(x, (y + height.value) / 2f),
        end = Offset(x + width.value, (y + height.value) / 2f),
        color = Color.Black,
        strokeWidth = 20.dp.value
    )
}

fun DrawScope.drawSpadesSuit(
    x: Float,
    y: Float,
    width: Dp,
    height: Dp
) {
    val path = Path().apply {
        moveTo(x, y + height.value)
        lineTo((x + width.value) / 2f, y)
        lineTo(x + width.value, y)
        lineTo(x, y + height.value)
        close()
    }
    drawPath(path, color = Color.Black)
}