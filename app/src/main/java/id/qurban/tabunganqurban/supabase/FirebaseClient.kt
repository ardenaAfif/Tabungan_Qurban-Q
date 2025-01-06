package id.qurban.tabunganqurban.supabase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.utils.Constant
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
        return firestore.collection("transaction").whereEqualTo("transactionId", transactionId)
            .get().await()
    }

    suspend fun getUserDetails(): User? {
        val userId = auth.currentUser?.uid ?: return null
        val userSnapshot = FirebaseFirestore.getInstance()
            .collection(Constant.Constant.USER_COLLECTION)
            .document(userId)
            .get()
            .await()
        return userSnapshot.toObject(User::class.java)
    }

    suspend fun getTotalAmountUser(): Double {
        val userId = auth.uid ?: throw IllegalStateException("User ID is Null")
        val historyCollection = firestore.collection("users").document(userId).collection("history")
        val querySnapshot = historyCollection.whereEqualTo("status", "Berhasil").get().await()

        // Tambahkan log untuk memastikan data ditemukan
        Log.d(">>FirebaseClient", "Documents found: ${querySnapshot.size()}")
        querySnapshot.documents.forEach { document ->
            Log.d(">>FirebaseClient", "Document data: ${document.data}")
        }

        return querySnapshot.documents.sumOf { it.toObject(Transaction::class.java)?.amount ?: 0.0 }
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
    suspend fun getUserTransactionHistory(userId: String): QuerySnapshot {
        return firestore.collection("users").document(userId)
            .collection("history").orderBy("dateCreated")
            .get().await()
    }

    suspend fun getPendingTransactionHistory(userId: String): QuerySnapshot {
        try {
            return firestore.collection("users").document(userId)
                .collection("history").whereEqualTo("status", "Pending")
                .orderBy("dateCreated")
                .get().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(">>Firestore", "Error in query: ${e.message}")
            throw e
        }
    }

    suspend fun getWaitingTransactionHistory(userId: String): QuerySnapshot {
        return firestore.collection("users").document(userId)
            .collection("history").whereEqualTo("status", "Menunggu Konfirmasi")
            .orderBy("dateCreated")
            .get().await()
    }

    suspend fun getAcceptedTransactionHistory(userId: String): QuerySnapshot {
        return firestore.collection("users").document(userId)
            .collection("history").whereEqualTo("status", "Berhasil")
            .orderBy("dateCreated")
            .get().await()
    }

    suspend fun getAllHistoryFromUsers(): QuerySnapshot {
        return firestore.collectionGroup("history").get().await()
    }
}