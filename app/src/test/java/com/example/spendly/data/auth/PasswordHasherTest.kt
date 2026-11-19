package com.example.spendly.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    private val hasher = PasswordHasher()

    @Test
    fun verify_accepts_the_right_password_only() {
        val hashed = hasher.hash("matkhau123")
        assertTrue(hasher.verify("matkhau123", hashed.hash, hashed.salt, hashed.iterations))
        assertFalse(hasher.verify("matkhau124", hashed.hash, hashed.salt, hashed.iterations))
        assertFalse(hasher.verify("", hashed.hash, hashed.salt, hashed.iterations))
    }

    @Test
    fun each_hash_uses_a_fresh_salt() {
        val a = hasher.hash("matkhau123")
        val b = hasher.hash("matkhau123")
        assertNotEquals(a.salt, b.salt)
        assertNotEquals(a.hash, b.hash)
    }

    @Test
    fun stores_iteration_count_so_it_can_be_raised_later() {
        val hashed = hasher.hash("matkhau123", iterations = 1_000)
        assertEquals(1_000, hashed.iterations)
        assertTrue(hasher.verify("matkhau123", hashed.hash, hashed.salt, 1_000))
        assertFalse(hasher.verify("matkhau123", hashed.hash, hashed.salt, 2_000))
    }

    @Test
    fun handles_unicode_passwords() {
        val hashed = hasher.hash("mậtkhẩu🔒123")
        assertTrue(hasher.verify("mậtkhẩu🔒123", hashed.hash, hashed.salt, hashed.iterations))
    }
}
