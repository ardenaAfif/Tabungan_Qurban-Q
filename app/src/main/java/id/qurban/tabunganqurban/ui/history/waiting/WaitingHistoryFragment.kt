package id.qurban.tabunganqurban.ui.history.waiting

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
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.adapter.HistoryAdapter
import id.qurban.tabunganqurban.databinding.FragmentWaitingHistoryBinding
import id.qurban.tabunganqurban.ui.history.HistoryVM
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WaitingHistoryFragment : Fragment() {

    private lateinit var binding: FragmentWaitingHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val historyVM: HistoryVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaitingHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeHistory()
        setupRvPendingHistory()

        historyVM.fetchTransactionByStatus("Menunggu Konfirmasi")
    }

    private fun observeHistory() {
        lifecycleScope.launchWhenStarted {
            historyVM.waitingHistory.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "Tunggu Sebentar...", Toast.LENGTH_SHORT).show()
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
        binding.rvWaitingTransaction.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}