package id.qurban.tabunganqurban.ui.detail.berhasil

import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ActivityDetailBerhasilNabungBinding
import id.qurban.tabunganqurban.databinding.ActivityDetailWaitingNabungBinding
import id.qurban.tabunganqurban.ui.history.HistoryActivity
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailBerhasilNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBerhasilNabungBinding

    private lateinit var transaction: Transaction
    private val profileViewModel: ProfileViewModel by viewModels()

    private val indicatorDrawables = listOf(
//        R.drawable.indicator_red,
        R.drawable.indicator_green,
        R.drawable.indicator_orange
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBerhasilNabungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = intent.getParcelableExtra("transaction")!!

        customToolbar()
        setupTransactionDetails()
        indicatorStatus()
        getUser()
    }

    private fun setupTransactionDetails() {
        binding.apply {
            tvInfoJumlahTransfer.text = formatCurrencyDouble(transaction.amount)
            tvIdTransaksiBerhasil.text = getString(R.string.id_transaction, transaction.transactionId.takeLast(5))
            tvIdTransaksiBerhasilFull.text = transaction.transactionId.takeLast(10)
            tvInfoTanggalTransfer.text = transaction.dateCreated
        }
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
                        Toast.makeText(this@DetailBerhasilNabungActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
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

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                val intent = Intent(this@DetailBerhasilNabungActivity, HistoryActivity::class.java)
                startActivity(intent)
                finish()
            }
            navBack.setImageDrawable(
                ContextCompat.getDrawable(
                    this@DetailBerhasilNabungActivity,
                    R.drawable.ic_close
                )
            )
            tvToolbarName.text = "Status Transaksi"
        }
    }
}