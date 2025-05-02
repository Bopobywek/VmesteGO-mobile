package ru.vmestego.routing

import androidx.compose.ui.graphics.vector.ImageVector

data class IconizedRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)