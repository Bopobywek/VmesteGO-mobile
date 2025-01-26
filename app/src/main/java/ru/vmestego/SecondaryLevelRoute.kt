package ru.vmestego

data class SecondaryLevelRoute<T : Any>(
    val name: String,
    val route: T,
    val localizedNameResourceId: Int
)