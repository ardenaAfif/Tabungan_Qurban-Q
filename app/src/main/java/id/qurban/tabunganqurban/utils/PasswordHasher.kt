package id.qurban.tabunganqurban.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {

    // Fungsi untuk meng-hash password
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    // Fungsi untuk memverifikasi password Hash
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}