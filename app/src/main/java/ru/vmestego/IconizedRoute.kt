package ru.vmestego

import androidx.compose.ui.graphics.vector.ImageVector

data class IconizedRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)