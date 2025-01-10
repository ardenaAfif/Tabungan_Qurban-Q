package id.qurban.tabunganqurban.ui.history.all

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.adapter.HistoryAdapter
import id.qurban.tabunganqurban.databinding.FragmentAllHistoryBinding
import id.qurban.tabunganqurban.ui.detail.berhasil.DetailBerhasilNabungActivity
import id.qurban.tabunganqurban.ui.detail.mengecek.DetailMengecekNabungActivity
import id.qurban.tabunganqurban.ui.detail.waiting.DetailWaitingNabungActivity
import id.qurban.tabunganqurban.ui.history.HistoryVM
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllHistoryFragment : Fragment() {

    private lateinit var binding: FragmentAllHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val historyVM: HistoryVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvAllHistory()
        observeHistory()
        handleTransactionListener()
    }

    private fun handleTransactionListener() {
        historyAdapter.setOnItemClickListener { transaction ->
            when (transaction.status.lowercase()) {
                "menunggu konfirmasi" -> {
                    val intent = Intent(requireContext(), DetailWaitingNabungActivity::class.java)
                    intent.putExtra("transaction", transaction)
                    startActivity(intent)
                    requireActivity().finish()
                }
                "mengecek" -> {
                    val intent = Intent(requireContext(), DetailMengecekNabungActivity::class.java)
                    intent.putExtra("transaction", transaction)
                    startActivity(intent)
                    requireActivity().finish()
                }
                "berhasil" -> {
                    val intent = Intent(requireContext(), DetailBerhasilNabungActivity::class.java)
                    intent.putExtra("transaction", transaction)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun observeHistory() {
        lifecycleScope.launchWhenStarted {
            historyVM.allHistory.collectLatest { resource ->
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

    private fun setupRvAllHistory() {
        historyAdapter = HistoryAdapter(requireContext())
        binding.rvAllTransaction.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}