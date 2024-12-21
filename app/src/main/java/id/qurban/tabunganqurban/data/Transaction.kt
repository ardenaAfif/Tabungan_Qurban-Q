package id.qurban.tabunganqurban.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class Transaction (
    val transactionId: String = "",
    val userId: String,
    val amount: Double,
    val buktiTransfer: String,
    val status: String,
    val dateCreated: String = ""
) : Parcelable {
    constructor() : this( "", "",0.0, "", "")
}