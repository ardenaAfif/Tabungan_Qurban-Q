package id.qurban.tabunganqurban.utils

import android.util.Log
import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        Log.d("PasswordHasher", "Verifying password: $password")
        Log.d("PasswordHasher", "With hash: $hashedPassword")
        val result = BCrypt.checkpw(password, hashedPassword)
        Log.d("PasswordHasher", "Verification result: $result")
        return result
    }
}
