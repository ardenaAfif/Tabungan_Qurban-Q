package id.qurban.tabunganqurban.supabase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.utils.Constant
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    fun updateTransaction(transactionId: String, imageUrl: String) {
        val userId = auth.uid ?: throw IllegalStateException("User ID is Null")

        val transactionRef = firestore.collection("users").document(userId)
            .collection("history").document(transactionId)

        transactionRef.update(
            "buktiTransfer", imageUrl,
            "status", "Mengecek"
        ).addOnSuccessListener {
            Log.d("FirebaseClient", "Transaksi berhasil diperbarui")
        }.addOnFailureListener { exception ->
            Log.e("FirebaseClient", "Gagal memperbarui transaksi: ${exception.message}")
        }
    }

    fun updateTransactionValidated(transactionId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val transactionRef = firestore.collectionGroup("history")
            .whereEqualTo("transactionId", transactionId)

        transactionRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    document.reference.update("status", "Berhasil")
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("Transaksi tidak ditemukan"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getTransaction(transactionId: String): Flow<Resource<Transaction>> = callbackFlow {
        val userId = auth.uid ?: throw IllegalStateException("User ID is Null")
        val transactionRef = firestore.collection("users").document(userId)
            .collection("history")
            .document(transactionId)

        val listenerRegistration = transactionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.message.toString()))
                return@addSnapshotListener
            }

            if (snapshot == null || !snapshot.exists()) {
                trySend(Resource.Error("Dokumen tidak ditemukan"))
                return@addSnapshotListener
            }

            val transaction = snapshot.toObject(Transaction::class.java)
            if (transaction != null) {
                trySend(Resource.Success(transaction))
            } else {
                trySend(Resource.Error("Gagal mengonversi dokumen"))
            }
        }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun getAllTransactions(): Flow<Resource<List<Transaction>>> = callbackFlow {

        val transactionRef = firestore.collectionGroup("history")
            .orderBy("status", Query.Direction.DESCENDING)
            .orderBy("dateCreated", Query.Direction.DESCENDING)

        val listenerRegistration = transactionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.message.toString()))
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(Resource.Error("Dokumen tidak ditemukan"))
                return@addSnapshotListener
            }

            val transactions = snapshot.toObjects(Transaction::class.java)
            trySend(Resource.Success(transactions))
        }

        awaitClose {
            listenerRegistration.remove()
        }
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

    /**
     * Mendapatkan total amount user dari sub-koleksi "history" dalam "users" secara real-time.
     */
    fun getTotalAmountUserFlow(): Flow<Double> = callbackFlow {
        val userId = auth.uid ?: throw IllegalStateException("User ID is Null")
        val historyCollection = firestore.collection("users").document(userId).collection("history")

        val listenerRegistration = historyCollection.whereEqualTo("status", "Berhasil")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(">>Firestore", "Error in query: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(0.0).isSuccess
                    return@addSnapshotListener
                }
                val totalAmount =
                    snapshot.documents.sumOf { it.toObject(Transaction::class.java)?.amount ?: 0.0 }
                trySend(totalAmount).isSuccess
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * Mendapatkan history transaksi dari sub-koleksi "history" dalam "users" secara real-time.
     */
    fun getUserTransactionHistory(userId: String): Flow<Resource<List<Transaction>>> =
        callbackFlow {
            val listenerRegistration = firestore.collection("users").document(userId)
                .collection("history").orderBy("dateCreated")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(">>Firestore", "Error in query: ${error.message}")
                        trySend(Resource.Error(error.message ?: "Unknown error")).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot == null) {
                        trySend(Resource.Error("Snapshot is null")).isSuccess
                        return@addSnapshotListener
                    }
                    val history = snapshot.toObjects(Transaction::class.java)
                    val sortedHistory = history.sortedByDescending { it.dateCreated }
                    trySend(Resource.Success(sortedHistory)).isSuccess
                }
            awaitClose {
                listenerRegistration.remove()
            }
        }

    fun getMengecekTransactionHistory(userId: String): Flow<Resource<List<Transaction>>> =
        callbackFlow {
            val listenerRegistration = firestore.collection("users").document(userId)
                .collection("history").whereEqualTo("status", "Mengecek")
                .orderBy("dateCreated")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(">>Firestore", "Error in query: ${error.message}")
                        trySend(Resource.Error(error.message ?: "Unknown error")).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot == null) {
                        trySend(Resource.Error("Snapshot is null")).isSuccess
                        return@addSnapshotListener
                    }
                    val history = snapshot.toObjects(Transaction::class.java)
                    val sortedHistory = history.sortedByDescending { it.dateCreated }
                    trySend(Resource.Success(sortedHistory)).isSuccess
                }
            awaitClose {
                listenerRegistration.remove()
            }
        }

    fun getWaitingTransactionHistory(userId: String): Flow<Resource<List<Transaction>>> =
        callbackFlow {
            val listenerRegistration = firestore.collection("users").document(userId)
                .collection("history").whereEqualTo("status", "Menunggu Konfirmasi")
                .orderBy("dateCreated")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(">>Firestore", "Error in query: ${error.message}")
                        trySend(Resource.Error(error.message ?: "Unknown error")).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot == null) {
                        trySend(Resource.Error("Snapshot is null")).isSuccess
                        return@addSnapshotListener
                    }
                    val history = snapshot.toObjects(Transaction::class.java)
                    val sortedHistory = history.sortedByDescending { it.dateCreated }
                    trySend(Resource.Success(sortedHistory)).isSuccess
                }
            awaitClose {
                listenerRegistration.remove()
            }
        }

    fun getAcceptedTransactionHistory(userId: String): Flow<Resource<List<Transaction>>> =
        callbackFlow {
            val listenerRegistration = firestore.collection("users").document(userId)
                .collection("history").whereEqualTo("status", "Berhasil")
                .orderBy("dateCreated")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(">>Firestore", "Error in query: ${error.message}")
                        trySend(Resource.Error(error.message ?: "Unknown error")).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot == null) {
                        trySend(Resource.Error("Snapshot is null")).isSuccess
                        return@addSnapshotListener
                    }
                    val history = snapshot.toObjects(Transaction::class.java)
                    val sortedHistory = history.sortedByDescending { it.dateCreated }
                    trySend(Resource.Success(sortedHistory)).isSuccess
                }
            awaitClose {
                listenerRegistration.remove()
            }
        }
}