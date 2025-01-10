package id.qurban.tabunganqurban.ui.detail.mengecek

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import id.qurban.tabunganqurban.ui.home.HomeFragment
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.FormatHelper.formatDate

class DetailMengecekNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMengecekNabungBinding

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
        binding = ActivityDetailMengecekNabungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = intent.getParcelableExtra("transaction")!!

        customToolbar()
        setupTransactionDetails()
        indicatorStatus()
        btnBottomListener()
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

    private fun setupTransactionDetails() {
        binding.apply {
            tvInfoJumlahTransfer.text = formatCurrencyDouble(transaction.amount + 1000.0)
            tvIdTransaksiMengecek.text = getString(R.string.id_transaction, transaction.transactionId.takeLast(5))
            tvInfoTanggalTransfer.text = transaction.dateCreated
        }
    }

    private fun btnBottomListener() {
        binding.apply {
            btnGoToHome.setOnClickListener {
                Intent(this@DetailMengecekNabungActivity, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            btnBatalkanTransaksi.setOnClickListener {
                Toast.makeText(this@DetailMengecekNabungActivity, "Sabar... belum jadi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                val intent = Intent(this@DetailMengecekNabungActivity, HistoryActivity::class.java)
                startActivity(intent)
                finish()
            }
            navBack.setImageDrawable(
                ContextCompat.getDrawable(
                    this@DetailMengecekNabungActivity,
                    R.drawable.ic_close
                )
            )
            tvToolbarName.text = "Status Transaksi"
        }
    }
}