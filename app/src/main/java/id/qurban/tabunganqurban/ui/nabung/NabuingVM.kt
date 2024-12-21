package id.qurban.tabunganqurban.ui.nabung

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.supabase.FirebaseClient
import id.qurban.tabunganqurban.utils.FormatHelper
import id.qurban.tabunganqurban.utils.FormatHelper.formatDate
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NabuingVM @Inject constructor(
    private val firebaseClient: FirebaseClient
): ViewModel() {

    private val _transaction = MutableStateFlow<Resource<Transaction>>(Resource.Unspecified())
    val transaction = _transaction.asStateFlow()

    fun addAmountTransaction(amount: Double, buktiTransfer: String) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val newTransaction = Transaction (
                    userId = userId ?: "",
                    amount = amount,
                    buktiTransfer = buktiTransfer,
                    status = "Pending",
                    dateCreated = formatDate()
                )
                firebaseClient.addTransaction(newTransaction).let { transactionId ->
                    _transaction.value = Resource.Success(newTransaction.copy(transactionId = transactionId.toString()))
                }
            } catch (e: Exception) {
                _transaction.value = Resource.Error(e.message.toString())
            }
        }
    }

    /**
     * getTransactionById.
     */
    fun getTransactionById(transactionId: String) {
        viewModelScope.launch {
            _transaction.value = Resource.Loading()
            try {
                val snapshot = firebaseClient.getTransactionById(transactionId)
                val transaction = snapshot?.documents?.firstOrNull()?.toObject(Transaction::class.java)
                if (transaction != null) {
                    _transaction.value = Resource.Success(transaction)
                } else {
                    _transaction.value = Resource.Error("Transaction not found")
                }
            } catch (e: Exception) {
                _transaction.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }


}