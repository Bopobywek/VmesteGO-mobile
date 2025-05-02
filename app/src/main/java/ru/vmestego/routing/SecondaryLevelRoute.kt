package ru.vmestego.routing

data class SecondaryLevelRoute<T : Any>(
    val name: String,
    val route: T,
    val localizedNameResourceId: Int
)