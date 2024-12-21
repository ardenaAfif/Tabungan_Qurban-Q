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
        viewModelScope.launch {
            _allHistory.emit(Resource.Loading())
            try {
                val result = firebaseClient.getUserTransactionHistory(auth.uid!!)
                val history = result.toObjects(Transaction::class.java)
                val sortedHistory = history.sortedByDescending { it.dateCreated }
                _allHistory.emit(Resource.Success(sortedHistory))
            } catch (e: Exception) {
                _allHistory.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    fun fetchTransactionByStatus(status: String) {
        viewModelScope.launch {
            val targetFlow = when (status) {
                "Pending" -> _pendingHistory
                "Menunggu Konfirmasi" -> _waitingHistory
                "Berhasil" -> _acceptedHistory
                else -> throw IllegalArgumentException("Unknown status: $status")
            }

            targetFlow.emit(Resource.Loading())
            try {
                val userId = auth.uid ?: throw Exception("User ID is null")
                val result = when (status) {
                    "Pending" -> firebaseClient.getPendingTransactionHistory(userId)
                    "Menunggu Konfirmasi" -> firebaseClient.getWaitingTransactionHistory(userId)
                    "Berhasil" -> firebaseClient.getAcceptedTransactionHistory(userId)
                    else -> throw IllegalArgumentException("Unknown status: $status")
                }
                val history = result.toObjects(Transaction::class.java)
                val sortedHistory = history.sortedByDescending { it.dateCreated }
                targetFlow.emit(Resource.Success(sortedHistory))
            } catch (e: Exception) {
                targetFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}