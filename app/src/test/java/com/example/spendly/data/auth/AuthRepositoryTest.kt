package com.example.spendly.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class InMemoryAccountStore : AccountStore {
    var accounts: List<UserAccount> = emptyList()
    var sessionId: String? = null
    var failWrites = false

    override fun loadAccounts() = accounts

    override fun saveAccounts(accounts: List<UserAccount>): Boolean {
        if (failWrites) return false
        this.accounts = accounts
        return true
    }

    override fun getPersistedSessionId() = sessionId

    override fun setPersistedSessionId(id: String?) {
        sessionId = id
    }
}

class AuthRepositoryTest {

    private lateinit var store: InMemoryAccountStore
    private lateinit var repo: AuthRepository
    private var nextId = 0

    @Before
    fun setUp() {
        store = InMemoryAccountStore()
        nextId = 0
        repo = AuthRepository(
            store = store,
            hasher = PasswordHasher(),
            clock = { 1_000L },
            idGenerator = { "id-${nextId++}" },
        )
    }

    private fun registerKinh(phone: String? = "0901234567") =
        repo.register("Nguyễn Minh Kinh", "Kinh@Example.com", phone, "matkhau123")

    @Test
    fun register_creates_account_with_hashed_password() {
        val result = registerKinh()
        assertTrue(result is AuthResult.Success)
        val saved = store.accounts.single()
        assertEquals("kinh@example.com", saved.email)
        assertEquals("0901234567", saved.phone)
        assertEquals("Nguyễn Minh Kinh", saved.fullName)
        assertEquals("Kinh", saved.displayName)
        assertNotEquals("matkhau123", saved.passwordHash)
        assertFalse(saved.passwordHash.contains("matkhau123"))
        assertEquals(PasswordHasher.DEFAULT_ITERATIONS, saved.iterations)
    }

    @Test
    fun register_does_not_log_the_user_in() {
        registerKinh()
        assertNull(repo.currentUser())
    }

    @Test
    fun register_rejects_duplicate_email_ignoring_case() {
        registerKinh()
        val again = repo.register("Người khác", "KINH@example.com", null, "matkhau456")
        assertEquals(AuthResult.Failure(AuthError.EMAIL_TAKEN), again)
        assertEquals(1, store.accounts.size)
    }

    @Test
    fun register_rejects_duplicate_phone_in_any_format() {
        registerKinh()
        val again = repo.register("Người khác", "khac@example.com", "+84 901 234 567", "matkhau456")
        assertEquals(AuthResult.Failure(AuthError.PHONE_TAKEN), again)
    }

    @Test
    fun register_without_phone_is_allowed_more_than_once() {
        assertTrue(repo.register("Một", "mot@example.com", null, "matkhau123") is AuthResult.Success)
        assertTrue(repo.register("Hai", "hai@example.com", "", "matkhau123") is AuthResult.Success)
        assertEquals(2, store.accounts.size)
    }

    @Test
    fun register_rejects_invalid_input_even_if_ui_skipped_validation() {
        assertEquals(
            AuthResult.Failure(AuthError.INVALID_INPUT),
            repo.register("A", "kinh@example.com", null, "matkhau123"),
        )
        assertEquals(
            AuthResult.Failure(AuthError.INVALID_INPUT),
            repo.register("Kinh", "khong-phai-email", null, "matkhau123"),
        )
        assertEquals(
            AuthResult.Failure(AuthError.INVALID_INPUT),
            repo.register("Kinh", "kinh@example.com", null, "12345678"),
        )
        assertTrue(store.accounts.isEmpty())
    }

    @Test
    fun register_reports_storage_failure() {
        store.failWrites = true
        assertEquals(AuthResult.Failure(AuthError.STORAGE_FAILED), registerKinh())
    }

    @Test
    fun same_password_gets_different_salt_and_hash() {
        repo.register("Một", "mot@example.com", null, "matkhau123")
        repo.register("Hai", "hai@example.com", null, "matkhau123")
        val (a, b) = store.accounts
        assertNotEquals(a.salt, b.salt)
        assertNotEquals(a.passwordHash, b.passwordHash)
    }

    @Test
    fun login_with_email_or_phone() {
        registerKinh()
        assertTrue(repo.login("kinh@example.com", "matkhau123", false) is AuthResult.Success)
        repo.logout()
        assertTrue(repo.login("  KINH@EXAMPLE.COM ", "matkhau123", false) is AuthResult.Success)
        repo.logout()
        assertTrue(repo.login("0901 234 567", "matkhau123", false) is AuthResult.Success)
        repo.logout()
        assertTrue(repo.login("+84901234567", "matkhau123", false) is AuthResult.Success)
    }

    @Test
    fun login_fails_with_the_same_error_for_wrong_password_and_unknown_account() {
        registerKinh()
        val wrongPassword = repo.login("kinh@example.com", "sai-mat-khau1", false)
        val unknown = repo.login("nguoila@example.com", "matkhau123", false)
        val malformed = repo.login("###", "matkhau123", false)
        val expected = AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
        assertEquals(expected, wrongPassword)
        assertEquals(expected, unknown)
        assertEquals(expected, malformed)
        assertNull(repo.currentUser())
    }

    @Test
    fun password_is_case_sensitive() {
        registerKinh()
        assertEquals(
            AuthResult.Failure(AuthError.INVALID_CREDENTIALS),
            repo.login("kinh@example.com", "MATKHAU123", false),
        )
    }

    @Test
    fun login_without_remember_keeps_session_in_memory_only() {
        registerKinh()
        repo.login("kinh@example.com", "matkhau123", remember = false)
        assertNotNull(repo.currentUser())
        assertNull(store.sessionId)

        // Mô phỏng mở lại ứng dụng: repository mới, cùng nơi lưu.
        val reopened = AuthRepository(store)
        assertNull(reopened.currentUser())
    }

    @Test
    fun login_with_remember_survives_app_restart() {
        registerKinh()
        repo.login("kinh@example.com", "matkhau123", remember = true)
        assertEquals("id-0", store.sessionId)

        val reopened = AuthRepository(store)
        assertEquals("kinh@example.com", reopened.currentUser()?.email)
    }

    @Test
    fun login_without_remember_clears_a_previously_remembered_session() {
        registerKinh()
        repo.login("kinh@example.com", "matkhau123", remember = true)
        repo.login("kinh@example.com", "matkhau123", remember = false)
        assertNull(store.sessionId)
    }

    @Test
    fun logout_clears_memory_and_persisted_session() {
        registerKinh()
        repo.login("kinh@example.com", "matkhau123", remember = true)
        repo.logout()
        assertNull(repo.currentUser())
        assertNull(store.sessionId)
        assertNull(AuthRepository(store).currentUser())
    }

    @Test
    fun stale_persisted_session_is_discarded() {
        store.sessionId = "tai-khoan-khong-con-ton-tai"
        assertNull(repo.currentUser())
        assertNull(store.sessionId)
    }

    @Test
    fun failed_login_does_not_replace_the_current_session() {
        registerKinh()
        repo.register("Hai", "hai@example.com", null, "matkhau123")
        repo.login("kinh@example.com", "matkhau123", false)
        repo.login("hai@example.com", "sai-mat-khau1", false)
        assertEquals("kinh@example.com", repo.currentUser()?.email)
    }
}
