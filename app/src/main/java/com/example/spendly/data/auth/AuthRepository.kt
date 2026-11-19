package com.example.spendly.data.auth

import android.content.Context
import com.example.spendly.domain.auth.AuthValidator
import java.util.UUID

enum class AuthError {
    /** Dữ liệu không qua kiểm tra (fragment đã kiểm tra, đây là lớp bảo vệ thứ hai). */
    INVALID_INPUT,
    EMAIL_TAKEN,
    PHONE_TAKEN,
    INVALID_CREDENTIALS,
    STORAGE_FAILED,
}

sealed class AuthResult {
    data class Success(val user: UserAccount) : AuthResult()
    data class Failure(val error: AuthError) : AuthResult()
}

/**
 * Đăng ký, đăng nhập và quản lý phiên, dữ liệu lưu cục bộ trên thiết bị.
 *
 * Phiên đăng nhập luôn được giữ trong bộ nhớ khi ứng dụng còn chạy; chỉ khi người dùng
 * tick "Lưu đăng nhập" thì mã tài khoản mới được ghi xuống để lần mở sau vào thẳng Trang chủ.
 */
class AuthRepository(
    private val store: AccountStore,
    private val hasher: PasswordHasher = PasswordHasher(),
    private val clock: () -> Long = System::currentTimeMillis,
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
) {

    @Volatile
    private var sessionUserId: String? = null

    /** Tạo tài khoản mới. Không tự đăng nhập: người dùng được chuyển về màn hình đăng nhập. */
    @Synchronized
    fun register(fullName: String, email: String, phone: String?, password: String): AuthResult {
        val normalizedPhone = phone?.takeIf { it.isNotBlank() }?.let(AuthValidator::normalizePhone)
        val invalid = AuthValidator.validateName(fullName) != null ||
            AuthValidator.validateEmail(email) != null ||
            AuthValidator.validateOptionalPhone(phone.orEmpty()) != null ||
            AuthValidator.validateNewPassword(password) != null
        if (invalid) return AuthResult.Failure(AuthError.INVALID_INPUT)

        val normalizedEmail = AuthValidator.normalizeEmail(email)
        val accounts = store.loadAccounts()
        if (accounts.any { it.email == normalizedEmail }) {
            return AuthResult.Failure(AuthError.EMAIL_TAKEN)
        }
        if (normalizedPhone != null && accounts.any { it.phone == normalizedPhone }) {
            return AuthResult.Failure(AuthError.PHONE_TAKEN)
        }

        val hashed = hasher.hash(password)
        val account = UserAccount(
            id = idGenerator(),
            fullName = AuthValidator.normalizeName(fullName),
            email = normalizedEmail,
            phone = normalizedPhone,
            passwordHash = hashed.hash,
            salt = hashed.salt,
            iterations = hashed.iterations,
            createdAt = clock(),
        )
        return if (store.saveAccounts(accounts + account)) {
            AuthResult.Success(account)
        } else {
            AuthResult.Failure(AuthError.STORAGE_FAILED)
        }
    }

    /**
     * Đăng nhập bằng email hoặc số điện thoại.
     * Sai tài khoản hay sai mật khẩu đều trả cùng một lỗi để không lộ tài khoản nào tồn tại.
     */
    @Synchronized
    fun login(identifier: String, password: String, remember: Boolean): AuthResult {
        val key = AuthValidator.normalizeIdentifier(identifier)
            ?: return AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
        val account = store.loadAccounts().firstOrNull { it.email == key || it.phone == key }
            ?: return AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
        if (!hasher.verify(password, account.passwordHash, account.salt, account.iterations)) {
            return AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
        }

        sessionUserId = account.id
        store.setPersistedSessionId(if (remember) account.id else null)
        return AuthResult.Success(account)
    }

    /** Người dùng đang đăng nhập, hoặc null nếu chưa đăng nhập / tài khoản không còn tồn tại. */
    @Synchronized
    fun currentUser(): UserAccount? {
        val id = sessionUserId ?: store.getPersistedSessionId() ?: return null
        val user = store.loadAccounts().firstOrNull { it.id == id }
        if (user == null) {
            sessionUserId = null
            store.setPersistedSessionId(null)
        } else {
            sessionUserId = id
        }
        return user
    }

    @Synchronized
    fun logout() {
        sessionUserId = null
        store.setPersistedSessionId(null)
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(SharedPrefsAccountStore(context)).also { instance = it }
            }
    }
}
