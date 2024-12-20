package id.qurban.tabunganqurban.supabase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import id.qurban.tabunganqurban.data.Transaction
import kotlinx.coroutines.tasks.await

class FirebaseClient {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun addTransaction(transaction: Transaction) {
        val userId = auth.uid ?: throw IllegalStateException("User ID is Null")
        val transactionRef = firestore.collection("transaction").document()
        val transactionWithId = transaction.copy(transactionId = transactionRef.id) // Set ID manual

        transactionRef.set(transactionWithId)
            .addOnSuccessListener {
                // Tambahkan ke sub-koleksi "history"
                firestore.collection("users").document(userId)
                    .collection("history")
                    .document(transactionRef.id)
                    .set(transactionWithId)
            }
            .addOnFailureListener { error ->
                Log.e("Error", "Failed to add to Transaction: ${error.message}")
            }
    }

    suspend fun getTransactionById(transactionId: String): QuerySnapshot? {
        return firestore.collection("transaction").whereEqualTo("transactionId", transactionId).get().await()
    }


    /**
     * Mendapatkan semua transaksi dari koleksi "transaction" berdasarkan userId.
     */
    suspend fun getTransaction(userId: String): QuerySnapshot? {
        return firestore.collection("transaction")
            .whereEqualTo("userId", userId)
            .get().await()
    }

    /**
     * Mendapatkan history transaksi dari sub-koleksi "history" dalam "users".
     */
    suspend fun getUserTransactionHistory(userId: String): QuerySnapshot? {
        return firestore.collection("users").document(userId)
            .collection("history")
            .get().await()
    }
}