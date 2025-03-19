package ru.vmestego.utils

class PasswordValidator {
    companion object {
        fun isValidPassword(password: String): Boolean {
            if (password.length < 8) return false
            if (password.firstOrNull { it.isDigit() } == null) return false
            if (password.filter { it.isLetter() }
                    .firstOrNull { it.isUpperCase() } == null) return false
            if (password.filter { it.isLetter() }
                    .firstOrNull { it.isLowerCase() } == null) return false
            if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false
            return true
        }
    }
}

