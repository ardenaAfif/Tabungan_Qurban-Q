package id.qurban.tabunganqurban.data

import kotlinx.serialization.Serializable

@Serializable
data class Transaction (
    val transactionId: String,
    val amount: Double,
    val buktiTransfer: String,
    val status: Boolean
)