package com.example.spendly.data.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Băm mật khẩu bằng PBKDF2-HMAC-SHA256 với muối ngẫu nhiên cho từng tài khoản.
 * Thuật toán này có sẵn từ Android 8 (API 26), trùng với minSdk của ứng dụng.
 */
class PasswordHasher(
    private val random: SecureRandom = SecureRandom(),
) {

    class Hashed(val hash: String, val salt: String, val iterations: Int)

    fun hash(password: String, iterations: Int = DEFAULT_ITERATIONS): Hashed {
        val salt = ByteArray(SALT_BYTES).also { random.nextBytes(it) }
        val hash = derive(password, salt, iterations)
        return Hashed(encode(hash), encode(salt), iterations)
    }

    fun verify(password: String, expectedHash: String, salt: String, iterations: Int): Boolean {
        val actual = derive(password, decode(salt), iterations)
        // So sánh thời gian không đổi để tránh lộ thông tin qua độ trễ.
        return MessageDigest.isEqual(actual, decode(expectedHash))
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    private fun decode(value: String): ByteArray = Base64.getDecoder().decode(value)

    companion object {
        const val DEFAULT_ITERATIONS = 100_000
        private const val ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val KEY_BITS = 256
        private const val SALT_BYTES = 16
    }
}
