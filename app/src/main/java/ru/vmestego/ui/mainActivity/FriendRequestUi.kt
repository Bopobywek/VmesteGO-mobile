package ru.vmestego.ui.mainActivity

import ru.vmestego.ui.models.UserUi

data class FriendRequestUi(
    val from: UserUi,
    val to: UserUi,
    val status: FriendRequestStatusUi,
    val id: Long
)

enum class FriendRequestStatusUi {
    Done, Pending, None, Rejected
}
