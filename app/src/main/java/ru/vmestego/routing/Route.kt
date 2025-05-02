package ru.vmestego.routing

data class Route<T : Any>(val name: String, val route: T)
