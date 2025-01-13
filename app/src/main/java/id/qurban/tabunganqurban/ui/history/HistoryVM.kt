package id.qurban.tabunganqurban.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    private val _mengecekHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val mengecekHistory: StateFlow<Resource<List<Transaction>>> = _mengecekHistory

    private val _waitingHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val waitingHistory: StateFlow<Resource<List<Transaction>>> = _waitingHistory

    private val _acceptedHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val acceptedHistory: StateFlow<Resource<List<Transaction>>> = _acceptedHistory

    private val _batalHistory = MutableStateFlow<Resource<List<Transaction>>>(Resource.Unspecified())
    val batalHistory: StateFlow<Resource<List<Transaction>>> = _batalHistory

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
            "Mengecek" -> _mengecekHistory
            "Menunggu Konfirmasi" -> _waitingHistory
            "Berhasil" -> _acceptedHistory
            "Dibatalkan" -> _batalHistory
            else -> throw IllegalArgumentException("Unknown status: $status")
        }

        viewModelScope.launch {
            targetFlow.emit(Resource.Loading())
            when (status) {
                "Mengecek" -> firebaseClient.getMengecekTransactionHistory(userId).collectLatest { resource ->
                    targetFlow.emit(resource)
                }
                "Menunggu Konfirmasi" -> firebaseClient.getWaitingTransactionHistory(userId).collectLatest { resource -> targetFlow.emit(resource)
                }
                "Berhasil" -> firebaseClient.getAcceptedTransactionHistory(userId).collectLatest { resource ->
                    targetFlow.emit(resource)
                }
                "Dibatalkan" -> firebaseClient.getBatalTransactionHistory(userId).collectLatest { resource ->
                    targetFlow.emit(resource)
                }
            }
        }
    }
}