package id.qurban.tabunganqurban.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val prodi: String,
    val semester: Int,
    val totalTabungan: Double
) : Parcelable {
    constructor(): this("", "",  "", "", 0, 0.0)
}