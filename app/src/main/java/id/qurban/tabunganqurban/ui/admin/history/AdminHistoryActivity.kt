package id.qurban.tabunganqurban.ui.admin.history

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.adapter.HistoryAdapter
import id.qurban.tabunganqurban.databinding.ActivityAdminHistoryBinding
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AdminHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val historyAdminVM: AdminHistoryVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRvHistory()
        observeHistory()
    }

    private fun observeHistory() {
        lifecycleScope.launchWhenStarted {
            historyAdminVM.allHistory.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        historyAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@AdminHistoryActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRvHistory() {
        historyAdapter = HistoryAdapter(this)
        binding.rvAllTransactionAdmin.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@AdminHistoryActivity)
        }
    }
}