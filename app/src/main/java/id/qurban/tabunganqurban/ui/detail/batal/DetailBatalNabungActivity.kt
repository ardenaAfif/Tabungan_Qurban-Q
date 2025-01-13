package id.qurban.tabunganqurban.ui.detail.batal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ActivityDetailBatalNabungBinding
import id.qurban.tabunganqurban.ui.history.HistoryActivity
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailBatalNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBatalNabungBinding

    private lateinit var transaction: Transaction
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBatalNabungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = intent.getParcelableExtra("transaction")!!

        customToolbar()
        setupTransactionDetails()
        getUser()
    }

    private fun getUser() {
        lifecycleScope.launchWhenStarted {
            profileViewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        binding.apply {
                            tvInfoNama.text = getString(
                                R.string.profile_name_format,
                                it.data?.firstName.orEmpty().toCamelCase(),
                                it.data?.lastName.orEmpty().toCamelCase())
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@DetailBatalNabungActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupTransactionDetails() {
        binding.apply {
            tvInfoJumlahTransfer.text = formatCurrencyDouble(transaction.amount)
            tvIdTransaksiBerhasil.text = getString(R.string.id_transaction, transaction.transactionId.takeLast(5))
            tvIdTransaksiBerhasilFull.text = transaction.transactionId.takeLast(10)
            tvInfoTanggalTransfer.text = transaction.dateCreated
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                val intent = Intent(this@DetailBatalNabungActivity, HistoryActivity::class.java)
                startActivity(intent)
                finish()
            }
            navBack.setImageDrawable(
                ContextCompat.getDrawable(
                    this@DetailBatalNabungActivity,
                    R.drawable.ic_close
                )
            )
            tvToolbarName.text = "Status Transaksi"
        }
    }
}