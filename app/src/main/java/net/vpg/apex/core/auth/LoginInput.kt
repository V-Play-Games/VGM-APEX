package net.vpg.apex.core.auth

import android.util.Patterns
import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern

data class LoginInput(
    val email: String,
    val password: String
)

val loginValidation = Validation {
    LoginInput::email {
        pattern(Patterns.EMAIL_ADDRESS.toRegex()) hint "Invalid email format"
    }
    LoginInput::password {
        minLength(6) hint "Password must be at least 6 characters"
        pattern(".*\\d.*".toRegex()) hint "Password must contain at least one number"
        pattern(".*[A-Z].*".toRegex()) hint "Password must contain at least one uppercase letter"
        pattern(".*[a-z].*".toRegex()) hint "Password must contain at least one lowercase letter"
        pattern(".*[^A-Za-z0-9].*".toRegex()) hint "Password must contain at least one special character"
    }
}

fun validateLoginInput(email: String, password: String) =
    loginValidation(LoginInput(email, password)).errors.firstOrNull()?.message

