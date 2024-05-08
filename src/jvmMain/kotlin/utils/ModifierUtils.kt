package utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

fun Modifier.bringToFront(): Modifier = zIndex(1f)
