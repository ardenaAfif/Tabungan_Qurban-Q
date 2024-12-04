package id.qurban.tabunganqurban.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val prodi: String,
    val semester: Int,
    val total_tabungan: Double,
//    val createdAt: Long = System.currentTimeMillis() // Default value untuk waktu saat ini
)

