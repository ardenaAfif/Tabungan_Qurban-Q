package id.qurban.tabunganqurban.ui.admin.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.qurban.tabunganqurban.supabase.FirebaseClient
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileAdminVM  @Inject constructor(
    private val firebaseClient: FirebaseClient
) : ViewModel() {

    private val _transactionStatusCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val transactionStatusCounts: StateFlow<Map<String, Int>> = _transactionStatusCounts

    init {
        fetchTransactionStatusCounts()
    }

    private fun fetchTransactionStatusCounts() {
        viewModelScope.launch {
            firebaseClient.getAllTransactions().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _transactionStatusCounts.emit(emptyMap())
                    }
                    is Resource.Success -> {
                        val transactions = resource.data ?: emptyList()
                        val statusCounts = transactions.groupingBy { it.status }.eachCount()
                        _transactionStatusCounts.emit(statusCounts)
                    }
                    is Resource.Error -> {
                        _transactionStatusCounts.emit(emptyMap())
                    }
                    else -> Unit
                }
            }
        }
    }
}