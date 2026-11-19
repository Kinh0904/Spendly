package com.example.spendly.domain.auth

/**
 * Lỗi kiểm tra dữ liệu nhập ở màn hình đăng ký / đăng nhập.
 * Lớp này không phụ thuộc Android nên có thể kiểm thử trực tiếp bằng JUnit.
 */
enum class ValidationError {
    NAME_EMPTY,
    NAME_TOO_SHORT,
    EMAIL_EMPTY,
    EMAIL_INVALID,
    PHONE_INVALID,
    PASSWORD_EMPTY,
    PASSWORD_WEAK,
    CONFIRM_EMPTY,
    CONFIRM_MISMATCH,
    IDENTIFIER_EMPTY,
    IDENTIFIER_INVALID,
}

object AuthValidator {

    const val MIN_NAME_LENGTH = 2
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 128

    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$"
    )

    // Số điện thoại di động Việt Nam sau khi chuẩn hóa: 0 + 9 chữ số.
    private val PHONE_REGEX = Regex("^0\\d{9}$")

    /** Gộp khoảng trắng thừa trong họ tên. */
    fun normalizeName(raw: String): String =
        raw.trim().replace(Regex("\\s+"), " ")

    /** Email không phân biệt hoa thường nên luôn lưu dạng chữ thường. */
    fun normalizeEmail(raw: String): String = raw.trim().lowercase()

    /**
     * Chuẩn hóa số điện thoại về dạng 0xxxxxxxxx.
     * Chấp nhận khoảng trắng, dấu chấm, gạch ngang, ngoặc và đầu số +84 / 84.
     */
    fun normalizePhone(raw: String): String {
        val compact = raw.trim().filter { it.isDigit() || it == '+' }
        return when {
            compact.startsWith("+84") -> "0" + compact.removePrefix("+84")
            compact.startsWith("84") && compact.length == 11 -> "0" + compact.removePrefix("84")
            else -> compact
        }
    }

    fun isEmail(value: String): Boolean =
        value.length <= 254 && EMAIL_REGEX.matches(value)

    fun isPhone(normalized: String): Boolean = PHONE_REGEX.matches(normalized)

    fun validateName(raw: String): ValidationError? {
        val name = normalizeName(raw)
        return when {
            name.isEmpty() -> ValidationError.NAME_EMPTY
            name.length < MIN_NAME_LENGTH -> ValidationError.NAME_TOO_SHORT
            else -> null
        }
    }

    fun validateEmail(raw: String): ValidationError? {
        val email = normalizeEmail(raw)
        return when {
            email.isEmpty() -> ValidationError.EMAIL_EMPTY
            !isEmail(email) -> ValidationError.EMAIL_INVALID
            else -> null
        }
    }

    /** Số điện thoại không bắt buộc: để trống là hợp lệ. */
    fun validateOptionalPhone(raw: String): ValidationError? {
        if (raw.isBlank()) return null
        return if (isPhone(normalizePhone(raw))) null else ValidationError.PHONE_INVALID
    }

    /** Mật khẩu mới: tối thiểu 8 ký tự, có cả chữ và số. */
    fun validateNewPassword(password: String): ValidationError? {
        if (password.isEmpty()) return ValidationError.PASSWORD_EMPTY
        val strong = password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH &&
            password.any { it.isLetter() } &&
            password.any { it.isDigit() }
        return if (strong) null else ValidationError.PASSWORD_WEAK
    }

    fun validateConfirmPassword(password: String, confirm: String): ValidationError? = when {
        confirm.isEmpty() -> ValidationError.CONFIRM_EMPTY
        confirm != password -> ValidationError.CONFIRM_MISMATCH
        else -> null
    }

    /** Ô "Email hoặc số điện thoại" ở màn hình đăng nhập. */
    fun validateIdentifier(raw: String): ValidationError? {
        if (raw.isBlank()) return ValidationError.IDENTIFIER_EMPTY
        return if (normalizeIdentifier(raw) != null) null else ValidationError.IDENTIFIER_INVALID
    }

    /**
     * Trả về email (chữ thường) hoặc số điện thoại đã chuẩn hóa,
     * hoặc null nếu giá trị không phải email lẫn số điện thoại hợp lệ.
     */
    fun normalizeIdentifier(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.contains('@')) {
            val email = normalizeEmail(trimmed)
            return if (isEmail(email)) email else null
        }
        val phone = normalizePhone(trimmed)
        return if (isPhone(phone)) phone else null
    }
}
