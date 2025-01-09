package id.qurban.tabunganqurban.ui.history.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.adapter.HistoryAdapter
import id.qurban.tabunganqurban.databinding.FragmentPendingHistoryBinding
import id.qurban.tabunganqurban.ui.history.HistoryVM
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MengecekHistoryFragment : Fragment() {

    private lateinit var binding: FragmentPendingHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val historyVM: HistoryVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPendingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeHistory()
        setupRvPendingHistory()

        historyVM.fetchTransactionByStatus("Mengecek")
    }

    private fun observeHistory() {
        lifecycleScope.launchWhenStarted {
            historyVM.pendingHistory.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        historyAdapter.differ.submitList(resource.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupRvPendingHistory() {
        historyAdapter = HistoryAdapter(requireContext())
        binding.rvPendingTransaction.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}