package id.qurban.tabunganqurban.ui.admin.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.supabase.FirebaseClient
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminHistoryVM @Inject constructor(
    private val firebaseClient: FirebaseClient
) : ViewModel() {

    private val _allHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val allHistory: StateFlow<Resource<List<Transaction>>> = _allHistory

    private val _transactionUpdateStatus = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val transactionUpdateStatus: StateFlow<Resource<Unit>> = _transactionUpdateStatus

    init {
        fetchAllTransaction()
    }

    private fun fetchAllTransaction() {
        viewModelScope.launch {
            _allHistory.emit(Resource.Loading())
            firebaseClient.getAllTransactions().collectLatest {
                _allHistory.emit(it)
            }
        }
    }

    fun updateTransactionValidated(transactionId: String) {

        firebaseClient.updateTransactionValidated(
            transactionId = transactionId,
            onSuccess = {
                _transactionUpdateStatus.value = Resource.Success(Unit)
            },
            onFailure = { exception ->
                _transactionUpdateStatus.value = Resource.Error(exception.message ?: "Gagal memperbarui transaksi")
            }
        )
    }
}