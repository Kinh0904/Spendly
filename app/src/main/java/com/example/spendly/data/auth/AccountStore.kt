package com.example.spendly.data.auth

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Nơi lưu danh sách tài khoản và phiên đăng nhập.
 * Tách thành interface để [AuthRepository] có thể kiểm thử mà không cần Android.
 */
interface AccountStore {
    fun loadAccounts(): List<UserAccount>
    fun saveAccounts(accounts: List<UserAccount>): Boolean

    fun getPersistedSessionId(): String?

    fun setPersistedSessionId(id: String?)
}

/** Lưu cục bộ bằng SharedPreferences riêng của ứng dụng (không chia sẻ với ứng dụng khác). */
class SharedPrefsAccountStore(context: Context) : AccountStore {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun loadAccounts(): List<UserAccount> {
        val json = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { toAccount(array.getJSONObject(it)) }
        } catch (_: JSONException) {
            emptyList()
        }
    }

    override fun saveAccounts(accounts: List<UserAccount>): Boolean {
        val array = JSONArray()
        accounts.forEach { array.put(toJson(it)) }
        return prefs.edit().putString(KEY_ACCOUNTS, array.toString()).commit()
    }

    override fun getPersistedSessionId(): String? = prefs.getString(KEY_SESSION_ID, null)

    override fun setPersistedSessionId(id: String?) {
        prefs.edit().apply {
            if (id == null) remove(KEY_SESSION_ID) else putString(KEY_SESSION_ID, id)
        }.apply()
    }

    private fun toJson(account: UserAccount) = JSONObject().apply {
        put("id", account.id)
        put("fullName", account.fullName)
        put("email", account.email)
        put("phone", account.phone ?: JSONObject.NULL)
        put("passwordHash", account.passwordHash)
        put("salt", account.salt)
        put("iterations", account.iterations)
        put("createdAt", account.createdAt)
    }

    private fun toAccount(obj: JSONObject) = UserAccount(
        id = obj.getString("id"),
        fullName = obj.getString("fullName"),
        email = obj.getString("email"),
        phone = if (obj.isNull("phone")) null else obj.getString("phone"),
        passwordHash = obj.getString("passwordHash"),
        salt = obj.getString("salt"),
        iterations = obj.getInt("iterations"),
        createdAt = obj.getLong("createdAt"),
    )

    companion object {
        /** Tên tệp này cũng được loại khỏi sao lưu trong backup_rules / data_extraction_rules. */
        const val PREFS_NAME = "spendly_auth_prefs"
        private const val KEY_ACCOUNTS = "accounts_json"
        private const val KEY_SESSION_ID = "session_user_id"
    }
}
