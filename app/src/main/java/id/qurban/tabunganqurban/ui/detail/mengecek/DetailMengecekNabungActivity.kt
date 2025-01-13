package id.qurban.tabunganqurban.ui.detail.mengecek

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ActivityDetailMengecekNabungBinding
import id.qurban.tabunganqurban.ui.MainActivity
import id.qurban.tabunganqurban.ui.history.HistoryActivity
import id.qurban.tabunganqurban.ui.nabung.NabungVM
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailMengecekNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMengecekNabungBinding
    private val nabungViewModel: NabungVM by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

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
        getUser()
        updateDataAsync()

        binding.ivPreviewBukiTransferCek.setOnClickListener {
            showImagePreviewDialog(transaction.buktiTransfer)
        }
    }

    private fun showImagePreviewDialog(imageUrl: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_bukti_tf_preview)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val ivImagePreview: ImageView = dialog.findViewById(R.id.ivImagePreview)
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)

        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(32)))
            .into(ivImagePreview)

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
                                it.data?.lastName.orEmpty().toCamelCase()
                            )
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            this@DetailMengecekNabungActivity,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
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
        val nextDrawable = ContextCompat.getDrawable(
            this,
            indicatorDrawables[(currentIndex + 1) % indicatorDrawables.size]
        )

        val transitionDrawable = TransitionDrawable(arrayOf(currentDrawable, nextDrawable))
        binding.indicatorStatus.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(1200) // Durasi transisi (1 detik)

        currentIndex = (currentIndex + 1) % indicatorDrawables.size
    }

    private fun setupTransactionDetails() {
        binding.apply {
            tvInfoJumlahTransfer.text = formatCurrencyDouble(transaction.amount + 1000.0)
            tvIdTransaksiMengecek.text =
                getString(R.string.id_transaction, transaction.transactionId.takeLast(5))
            tvInfoTanggalTransfer.text = transaction.dateCreated
            Glide.with(this@DetailMengecekNabungActivity)
                .load(transaction.buktiTransfer)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(21)))
                .into(ivPreviewBukiTransferCek)
        }
    }

    private fun updateDataAsync() {
        Handler(Looper.getMainLooper()).postDelayed({
            // Memperbarui data dari Firebase
            nabungViewModel.getTransaction(transaction.transactionId)
            lifecycleScope.launchWhenStarted {
                nabungViewModel.transaction.collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            // Menampilkan indikator loading jika diperlukan
                        }

                        is Resource.Success -> {
                            result.data?.let {
                                transaction = it
                                setupTransactionDetails()
                                // Memuat gambar bukti transfer
                                Glide.with(this@DetailMengecekNabungActivity)
                                    .load(transaction.buktiTransfer)
                                    .apply(RequestOptions.bitmapTransform(RoundedCorners(21)))
                                    .into(binding.ivPreviewBukiTransferCek)
                            }
                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                this@DetailMengecekNabungActivity,
                                "Gagal memperbarui data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }, 100)
    }

    private fun btnBottomListener() {
        binding.apply {
            btnGoToHome.setOnClickListener {
                val intent = Intent(this@DetailMengecekNabungActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()

            }
            btnBatalkanTransaksi.setOnClickListener {
                nabungViewModel.updateBatalTransaction(transaction.transactionId)
                val intent = Intent(this@DetailMengecekNabungActivity, HistoryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                gotoHistory()
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

    override fun onBackPressed() {
        super.onBackPressed()
        gotoHistory()
    }

    private fun gotoHistory() {
        val intent = Intent(this@DetailMengecekNabungActivity, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }
}