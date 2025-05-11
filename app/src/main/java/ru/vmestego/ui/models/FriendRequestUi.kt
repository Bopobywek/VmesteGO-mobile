package ru.vmestego.ui.models

data class FriendRequestUi(
    val from: UserUi,
    val to: UserUi,
    val status: FriendRequestStatusUi,
    val id: Long
)

enum class FriendRequestStatusUi {
    Done, Pending, None, Rejected
}