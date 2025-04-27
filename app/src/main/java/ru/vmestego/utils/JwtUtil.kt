package ru.vmestego.utils

import com.auth0.jwt.JWT
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class JwtUtil {
    companion object {
        fun getUserIdFromToken(token: String): String? {
            val decoded = JWT.decode(token)
            return decoded.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").asString()
        }

        fun getRoleFromToken(token: String): String? {
            val decoded = JWT.decode(token)
            return decoded.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asString()
        }
    }
}