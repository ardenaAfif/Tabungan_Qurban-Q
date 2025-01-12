package id.qurban.tabunganqurban.ui.admin.history

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.adapter.HistoryAdapter
import id.qurban.tabunganqurban.databinding.ActivityAdminHistoryBinding
import id.qurban.tabunganqurban.ui.admin.detail.DetailTransaksiAdminActivity
import id.qurban.tabunganqurban.ui.admin.profile.ProfileAdminActivity
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

        setSupportActionBar(binding.toolbar)

        setupRvHistory()
        observeHistory()
        handleTransactionListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                val intent = Intent(this, ProfileAdminActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleTransactionListener() {
        historyAdapter.setOnItemClickListener {
            val intent = Intent(this, DetailTransaksiAdminActivity::class.java)
            intent.putExtra("transaction", it)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
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