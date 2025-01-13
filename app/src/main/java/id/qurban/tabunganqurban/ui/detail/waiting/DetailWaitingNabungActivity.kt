package id.qurban.tabunganqurban.ui.detail.waiting

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ActivityDetailWaitingNabungBinding
import id.qurban.tabunganqurban.ui.detail.mengecek.DetailMengecekNabungActivity
import id.qurban.tabunganqurban.ui.history.HistoryActivity
import id.qurban.tabunganqurban.ui.nabung.NabungVM
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrencyDouble
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailWaitingNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailWaitingNabungBinding
    private var selectedImageUri: Uri? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val profileViewModel: ProfileViewModel by viewModels()
    private val nabungViewModel: NabungVM by viewModels()

    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailWaitingNabungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = intent.getParcelableExtra("transaction")!!

        customToolbar()
        btnCopyListener()
        getUser()
        setupTransactionDetails()
        batalkanListener()

        binding.btnSudahTransfer.setOnClickListener {
            showUploadBottomSheet()
        }

    }

    private fun batalkanListener() {
        binding.btnBatalkanTransaksi.setOnClickListener {
            nabungViewModel.updateBatalTransaction(transaction.transactionId)
            val intent = Intent(this@DetailWaitingNabungActivity, HistoryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun setupTransactionDetails() {
        binding.apply {
            tvJumlahTransfer.text = formatCurrencyDouble(transaction.amount + 1000.0)
            tvInfoIdTransaksi.text = getString(R.string.id_transaction, transaction.transactionId.takeLast(5))
            tvInfoJumlahNabung.text = formatCurrencyDouble(transaction.amount)
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
                        Toast.makeText(this@DetailWaitingNabungActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showUploadBottomSheet() {
        // Buat dialog jika belum ada
        if (bottomSheetDialog == null) {
            bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_upload, null)
            bottomSheetDialog?.setContentView(view)

            // Set rounded corners for the BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.background = ContextCompat.getDrawable(this, R.drawable.bg_bottom_sheet)

            bottomSheet?.elevation = 0f

            val btnUpload = view.findViewById<LinearLayout>(R.id.btnUploadBukti)
            val ivPreview = view.findViewById<ImageView>(R.id.ivPreviewBukiTransfer)
            val btnLanjutkan = view.findViewById<Button>(R.id.btnLanjutkan)
            val pbTransfer = view.findViewById<ProgressBar>(R.id.pbTransfer)

            // Handle klik pada tombol upload
            btnUpload.setOnClickListener {
                openImagePicker()
            }

            // Handle tombol lanjutkan
            btnLanjutkan.setOnClickListener {
                if (selectedImageUri == null) {
                    Toast.makeText(this, "Silakan upload bukti transfer dahulu", Toast.LENGTH_SHORT).show()
                } else {
                    btnLanjutkan.visibility = View.GONE
                    pbTransfer.visibility = View.VISIBLE
                    uploadImageToFirebaseStorage()
                }
            }
        }

        // Perbarui tampilan sesuai status gambar
        val ivPreview = bottomSheetDialog?.findViewById<ImageView>(R.id.ivPreviewBukiTransfer)
        val btnUpload = bottomSheetDialog?.findViewById<LinearLayout>(R.id.btnUploadBukti)

        if (selectedImageUri != null) {
            ivPreview?.visibility = View.VISIBLE
            ivPreview?.setImageURI(selectedImageUri)
        } else {
            ivPreview?.visibility = View.GONE
            btnUpload?.visibility = View.VISIBLE
        }

        bottomSheetDialog?.show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                // Simpan URI gambar yang dipilih
                selectedImageUri = imageUri

                // Perbarui ImageView dalam dialog jika dialog terbuka
                val ivPreview = bottomSheetDialog?.findViewById<ImageView>(R.id.ivPreviewBukiTransfer)
                if (ivPreview != null) {
                    ivPreview.visibility = View.VISIBLE
                    Glide.with(this@DetailWaitingNabungActivity)
                        .load(selectedImageUri)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(21)))
                        .into(ivPreview)
                }
            } else {
                Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        selectedImageUri?.let { uri ->
            val storageRef = FirebaseStorage.getInstance().reference
            val transactionId = transaction.transactionId
            val imageRef = storageRef.child("buktiTransaksi/$transactionId.jpg")

            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Mendapatkan URL gambar yang diunggah
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // Simpan URL gambar ke Firestore
                    nabungViewModel.updateMengecekTransaction(transactionId, imageUrl.toString())
                    Toast.makeText(this, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog?.dismiss()
                    val intent = Intent(this@DetailWaitingNabungActivity, DetailMengecekNabungActivity::class.java)
                    intent.putExtra("transaction", transaction)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal mendapatkan URL gambar", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                onBackPressed()
            }
            navBack.setImageDrawable(ContextCompat.getDrawable(this@DetailWaitingNabungActivity, R.drawable.ic_close))
            tvToolbarName.text = "Transfer Sekarang"
        }
    }

    private fun btnCopyListener() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Listener untuk tombol salin nomor rekening
        binding.btnCopyNoRek.setOnClickListener {
            val noRekening = binding.tvNoRekening.text.toString()
            val clip = ClipData.newPlainText("No Rekening", noRekening)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "No. Rekening disalin: $noRekening", Toast.LENGTH_SHORT).show()
        }

        // Listener untuk tombol salin jumlah transfer
        binding.btnCopyJumlahTransfer.setOnClickListener {
            val jumlahTransfer = (transaction.amount + 1000).toInt()
            val clip = ClipData.newPlainText("Jumlah Transfer", jumlahTransfer.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Jumlah Transfer disalin: ${formatCurrencyDouble(transaction.amount + 1000)}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        const val  PICK_IMAGE_REQUEST_CODE = 1001
    }

}