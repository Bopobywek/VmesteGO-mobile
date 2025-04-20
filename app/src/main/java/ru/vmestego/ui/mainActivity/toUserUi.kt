package ru.vmestego.ui.mainActivity

import ru.vmestego.bll.services.users.models.UserResponse

fun UserResponse.toUserUi(): UserUi {
    return UserUi(
        name = this.username,
        id = this.id.toLong(),
        imageUrl = this.imageUrl
    )
}