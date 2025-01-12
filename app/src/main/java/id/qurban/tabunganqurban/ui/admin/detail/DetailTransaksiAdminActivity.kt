package id.qurban.tabunganqurban.ui.admin.detail

import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ActivityDetailTransaksiAdminBinding
import id.qurban.tabunganqurban.ui.MainActivity
import id.qurban.tabunganqurban.ui.admin.history.AdminHistoryVM
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailTransaksiAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTransaksiAdminBinding
    private val historyAdminVM: AdminHistoryVM by viewModels()

    private lateinit var transaction: Transaction
    private val indicatorDrawables = listOf(
        R.drawable.indicator_red,
        R.drawable.indicator_green,
        R.drawable.indicator_orange,
        R.drawable.line_home_indicator
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransaksiAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = intent.getParcelableExtra("transaction")!!

        customToolbar()
        indicatorStatus()
        btnBottomListener()
        observeViewModel()
        setupTransactionDetails()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            historyAdminVM.transactionUpdateStatus.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Toast.makeText(this@DetailTransaksiAdminActivity, "Sedang memperbarui status...", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        Toast.makeText(this@DetailTransaksiAdminActivity, "Status berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        finish() // Tutup Activity setelah berhasil
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@DetailTransaksiAdminActivity, "Gagal memperbarui: ${resource.message}", Toast.LENGTH_SHORT).show()
                        Log.d(">>Admin", "Gagal memperbarui: ${resource.message}")
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setupTransactionDetails() {
        binding.apply {
            tvInfoJumlahTransfer.text = formatCurrencyDouble(transaction.amount + 1000.0)
            tvIdTransaksi.text = transaction.transactionId.takeLast(10)
            tvIdUser.text = transaction.userId.takeLast(10)
            tvInfoTanggalTransfer.text = transaction.dateCreated
            tvStatusTransaksi.text = transaction.status
            statusTransactionColor()
            Glide.with(this@DetailTransaksiAdminActivity)
                .load(transaction.buktiTransfer)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(21)))
                .into(ivPreviewBukiTransferCek)

            linearLayoutBtn.visibility = if (transaction.status == "Mengecek") View.VISIBLE else View.GONE
        }
    }

    private fun statusTransactionColor() {
        binding.apply {
            when (transaction.status.lowercase()) {
                "menunggu konfirmasi" -> tvStatusTransaksi.setTextColor(this@DetailTransaksiAdminActivity.getColor(R.color.red))
                "mengecek" -> tvStatusTransaksi.setTextColor(this@DetailTransaksiAdminActivity.getColor(R.color.orange))
                "berhasil" -> tvStatusTransaksi.setTextColor(this@DetailTransaksiAdminActivity.getColor(R.color.primary))
                else -> tvStatusTransaksi.setTextColor(this@DetailTransaksiAdminActivity.getColor(R.color.grey))
            }
        }
    }

    private fun btnBottomListener() {
        binding.apply {
            btnValidated.setOnClickListener {
                historyAdminVM.updateTransactionValidated(transaction.transactionId)
            }
            btnBatalkanTransaksi.setOnClickListener {
                Toast.makeText(this@DetailTransaksiAdminActivity, "Sabar... belum jadi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                onBackPressed()
            }
            tvToolbarName.text = "Konfirmasi Transaksi"
        }
    }

    private fun indicatorStatus() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                animateDrawableChange()
                handler.postDelayed(this, 1500)
            }
        }
        handler.post(runnable)
    }

    private fun animateDrawableChange() {
        val currentDrawable = ContextCompat.getDrawable(this, indicatorDrawables[currentIndex])
        val nextDrawable = ContextCompat.getDrawable(this, indicatorDrawables[(currentIndex + 1) % indicatorDrawables.size])

        val transitionDrawable = TransitionDrawable(arrayOf(currentDrawable, nextDrawable))
        binding.indicatorStatus.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(1200) // Durasi transisi (1 detik)

        currentIndex = (currentIndex + 1) % indicatorDrawables.size
    }
}