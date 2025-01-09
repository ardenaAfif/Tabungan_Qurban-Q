package id.qurban.tabunganqurban.ui.detail.mengecek

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ActivityDetailMengecekNabungBinding
import id.qurban.tabunganqurban.ui.MainActivity
import id.qurban.tabunganqurban.ui.history.HistoryActivity
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.FormatHelper.formatDate

class DetailMengecekNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMengecekNabungBinding

    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMengecekNabungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = intent.getParcelableExtra("transaction")!!

        customToolbar()
        setupTransactionDetails()
    }

    private fun setupTransactionDetails() {
        binding.apply {
            tvInfoJumlahTransfer.text = formatCurrencyDouble(transaction.amount + 1000.0)
            tvIdTransaksiMengecek.text = "ID#${transaction.transactionId.takeLast(5)}"
            tvInfoTanggalTransfer.text = transaction.dateCreated
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                val intent = Intent(this@DetailMengecekNabungActivity, HistoryActivity::class.java)
                startActivity(intent)
            }
            navBack.setImageDrawable(ContextCompat.getDrawable(this@DetailMengecekNabungActivity, R.drawable.ic_close))
            tvToolbarName.text = "Status Transaksi"
        }
    }
}