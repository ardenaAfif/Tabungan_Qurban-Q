package id.qurban.tabunganqurban.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.supabase.FirebaseClient
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val allHistory: StateFlow<Resource<List<Transaction>>> = _allHistory

    private val _pendingHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val pendingHistory: StateFlow<Resource<List<Transaction>>> = _pendingHistory

    private val _waitingHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val waitingHistory: StateFlow<Resource<List<Transaction>>> = _waitingHistory

    private val _acceptedHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val acceptedHistory: StateFlow<Resource<List<Transaction>>> = _acceptedHistory

    init {
        fetchAllTransaction()
    }

    private fun fetchAllTransaction() {
        val userId = auth.uid ?: return
        viewModelScope.launch {
            _allHistory.emit(Resource.Loading())
            firebaseClient.getUserTransactionHistory(userId).collectLatest { resource ->
                _allHistory.emit(resource)
            }
        }
    }

    fun fetchTransactionByStatus(status: String) {
        val userId = auth.uid ?: return
        val targetFlow = when (status) {
            "Pending" -> _pendingHistory
            "Menunggu Konfirmasi" -> _waitingHistory
            "Berhasil" -> _acceptedHistory
            else -> throw IllegalArgumentException("Unknown status: $status")
        }

        viewModelScope.launch {
            targetFlow.emit(Resource.Loading())
            when (status) {
                "Pending" -> firebaseClient.getPendingTransactionHistory(userId).collectLatest { resource ->
                    targetFlow.emit(resource)
                }
                "Menunggu Konfirmasi" -> firebaseClient.getWaitingTransactionHistory(userId).collectLatest { resource ->
                    targetFlow.emit(resource)
                }
                "Berhasil" -> firebaseClient.getAcceptedTransactionHistory(userId).collectLatest { resource ->
                    targetFlow.emit(resource)
                }
            }
        }
    }
}