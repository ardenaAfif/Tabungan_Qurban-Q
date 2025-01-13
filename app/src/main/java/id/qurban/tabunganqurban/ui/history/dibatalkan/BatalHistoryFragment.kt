package id.qurban.tabunganqurban.ui.history.dibatalkan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.adapter.HistoryAdapter
import id.qurban.tabunganqurban.databinding.FragmentBatalHistoryBinding
import id.qurban.tabunganqurban.ui.detail.batal.DetailBatalNabungActivity
import id.qurban.tabunganqurban.ui.history.HistoryVM
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BatalHistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var binding: FragmentBatalHistoryBinding
    private val historyVM: HistoryVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBatalHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeHistory()
        setupRvBatalHistory()
        handleTransactionListener()

        historyVM.fetchTransactionByStatus("Dibatalkan")
    }

    private fun observeHistory() {
        lifecycleScope.launchWhenStarted {
            historyVM.batalHistory.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        val transactions = resource.data ?: emptyList()
                        binding.apply {
                            if (transactions.isEmpty()) {
                                layoutNoData.visibility = View.VISIBLE
                                rvBatalTransaction.visibility = View.GONE
                            } else {
                                layoutNoData.visibility = View.GONE
                                historyAdapter.differ.submitList(resource.data)
                            }
                        }
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

    private fun setupRvBatalHistory() {
        historyAdapter = HistoryAdapter(requireContext())
        binding.rvBatalTransaction.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleTransactionListener() {
        historyAdapter.setOnItemClickListener { transaction ->
            when (transaction.status.lowercase()) {
                "dibatalkan" -> {
                    val intent = Intent(requireContext(), DetailBatalNabungActivity::class.java)
                    intent.putExtra("transaction", transaction)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}