package com.example.spendly.presentation.auth

import androidx.annotation.StringRes
import com.example.spendly.R
import com.example.spendly.data.auth.AuthError
import com.example.spendly.domain.auth.ValidationError

/** Ánh xạ lỗi kiểm tra dữ liệu sang chuỗi hiển thị. */
@StringRes
fun ValidationError.messageRes(): Int = when (this) {
    ValidationError.NAME_EMPTY -> R.string.auth_error_name_empty
    ValidationError.NAME_TOO_SHORT -> R.string.auth_error_name_short
    ValidationError.EMAIL_EMPTY -> R.string.auth_error_email_empty
    ValidationError.EMAIL_INVALID -> R.string.auth_error_email_invalid
    ValidationError.PHONE_INVALID -> R.string.auth_error_phone_invalid
    ValidationError.PASSWORD_EMPTY -> R.string.auth_error_password_empty
    ValidationError.PASSWORD_WEAK -> R.string.auth_error_password_weak
    ValidationError.CONFIRM_EMPTY -> R.string.auth_error_confirm_empty
    ValidationError.CONFIRM_MISMATCH -> R.string.auth_error_confirm_mismatch
    ValidationError.IDENTIFIER_EMPTY -> R.string.auth_error_identifier_empty
    ValidationError.IDENTIFIER_INVALID -> R.string.auth_error_identifier_invalid
}

/** Ánh xạ lỗi nghiệp vụ (từ AuthRepository) sang chuỗi hiển thị. */
@StringRes
fun AuthError.messageRes(): Int = when (this) {
    AuthError.EMAIL_TAKEN -> R.string.auth_error_email_taken
    AuthError.PHONE_TAKEN -> R.string.auth_error_phone_taken
    AuthError.INVALID_CREDENTIALS -> R.string.auth_error_credentials
    AuthError.INVALID_INPUT,
    AuthError.STORAGE_FAILED -> R.string.auth_error_generic
}
