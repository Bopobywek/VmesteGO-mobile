package ru.vmestego

data class FriendRequestUi(
    val from: UserUi,
    val to: UserUi,
    val status: String,
    val id: Long
)