package id.qurban.tabunganqurban.ui.nabung

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.supabase.FirebaseClient
import id.qurban.tabunganqurban.utils.FormatHelper.formatDate
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NabungVM @Inject constructor(
    private val firebaseClient: FirebaseClient
): ViewModel() {

    private val _transaction = MutableStateFlow<Resource<Transaction>>(Resource.Unspecified())
    val transaction = _transaction.asStateFlow()

    private val _allTransactions = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val allTransactions = _allTransactions.asStateFlow()

    fun addAmountTransaction(amount: Double, buktiTransfer: String) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val newTransaction = Transaction (
                    userId = userId ?: "",
                    amount = amount,
                    buktiTransfer = buktiTransfer,
                    status = "Menunggu Konfirmasi",
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

    fun updateTransaction(transactionId: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                firebaseClient.updateTransaction(transactionId, imageUrl)
            } catch (e: Exception) {
                _transaction.value = Resource.Error(e.message.toString())
            }
        }
    }

    fun getTransaction(transactionId: String) {
        viewModelScope.launch {
            firebaseClient.getTransaction(transactionId).collectLatest { resource ->
                _transaction.value = resource
            }
        }
    }

    fun getAllTransactions() {
        viewModelScope.launch {
            firebaseClient.getAllTransactions().collectLatest { resource ->
                _allTransactions.value = resource
            }
        }
    }
}