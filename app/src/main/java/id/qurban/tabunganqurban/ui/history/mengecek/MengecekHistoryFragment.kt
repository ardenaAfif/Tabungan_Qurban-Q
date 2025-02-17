package id.qurban.tabunganqurban.ui.history.mengecek

import android.content.Intent
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
import id.qurban.tabunganqurban.databinding.FragmentMengecekHistoryBinding
import id.qurban.tabunganqurban.databinding.FragmentWaitingHistoryBinding
import id.qurban.tabunganqurban.ui.detail.berhasil.DetailBerhasilNabungActivity
import id.qurban.tabunganqurban.ui.detail.mengecek.DetailMengecekNabungActivity
import id.qurban.tabunganqurban.ui.detail.waiting.DetailWaitingNabungActivity
import id.qurban.tabunganqurban.ui.history.HistoryVM
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MengecekHistoryFragment : Fragment() {

    private lateinit var binding: FragmentMengecekHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val historyVM: HistoryVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMengecekHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeHistory()
        setupRvPendingHistory()
        handleTransactionListener()

        historyVM.fetchTransactionByStatus("Mengecek")
    }

    private fun handleTransactionListener() {
        historyAdapter.setOnItemClickListener { transaction ->
            when (transaction.status.lowercase()) {
                "mengecek" -> {
                    val intent = Intent(requireContext(), DetailMengecekNabungActivity::class.java)
                    intent.putExtra("transaction", transaction)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
            }
        }
    }

    private fun observeHistory() {
        lifecycleScope.launchWhenStarted {
            historyVM.mengecekHistory.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val transactions = resource.data ?: emptyList()
                        binding.apply {
                            if (transactions.isEmpty()) {
                                layoutNoData.visibility = View.VISIBLE
                                rvMengecekTransaction.visibility = View.GONE
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

    private fun setupRvPendingHistory() {
        historyAdapter = HistoryAdapter(requireContext())
        binding.rvMengecekTransaction.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}