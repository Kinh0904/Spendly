package com.example.spendly.data.auth

/**
 * Tài khoản lưu trên thiết bị. Mật khẩu không bao giờ được lưu dạng thô:
 * chỉ lưu [passwordHash] (PBKDF2) cùng [salt] và số vòng lặp [iterations].
 */
data class UserAccount(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val passwordHash: String,
    val salt: String,
    val iterations: Int,
    val createdAt: Long,
) {
    /** Tên gọi ngắn dùng trong lời chào, ví dụ "Nguyễn Minh Kinh" -> "Kinh". */
    val displayName: String
        get() = fullName.trim().substringAfterLast(' ')
}
