package ru.vmestego.bll.exceptions

import io.ktor.http.HttpStatusCode

class HttpServiceException(
    val statusCode: HttpStatusCode?,
    val errorMessage: String?
) : Exception()

