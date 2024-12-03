package id.qurban.tabunganqurban.ui.nabung

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.ActivityDetailNabungBinding
import id.qurban.tabunganqurban.ui.MainActivity

class DetailNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailNabungBinding
    private var selectedImageUri: Uri? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNabungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customToolbar()
        btnCopyListener()

        binding.btnSudahTransfer.setOnClickListener {
            showUploadBottomSheet()
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

            val btnUpload = view.findViewById<LinearLayout>(R.id.btnUploadBukti)
            val ivPreview = view.findViewById<ImageView>(R.id.ivPreviewBukiTransfer)
            val btnLanjutkan = view.findViewById<Button>(R.id.btnLanjutkan)

            // Handle klik pada tombol upload
            btnUpload.setOnClickListener {
                openImagePicker()
            }

            // Handle tombol lanjutkan
            btnLanjutkan.setOnClickListener {
                Toast.makeText(this, "Lanjutkan diklik", Toast.LENGTH_SHORT).show()
                bottomSheetDialog?.dismiss()
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
                Toast.makeText(this, "Gambar berhasil di-upload", Toast.LENGTH_SHORT).show()

                // Perbarui ImageView dalam dialog jika dialog terbuka
                val ivPreview = bottomSheetDialog?.findViewById<ImageView>(R.id.ivPreviewBukiTransfer)
                if (ivPreview != null) {
                    ivPreview.visibility = View.VISIBLE
                    ivPreview.setImageURI(selectedImageUri)
                }
            } else {
                Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                val intent = Intent(this@DetailNabungActivity, MainActivity::class.java)
                startActivity(intent)
            }
            navBack.setImageDrawable(ContextCompat.getDrawable(this@DetailNabungActivity, R.drawable.ic_close))
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
            val jumlahTransfer = binding.tvJumlahTransfer.text.toString()
            val clip = ClipData.newPlainText("Jumlah Transfer", jumlahTransfer)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Jumlah Transfer disalin: $jumlahTransfer", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToHome()
    }

    private fun navigateToHome() {
        val intent = Intent(this@DetailNabungActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("navigateTo", "HomeFragment")
        }
        startActivity(intent)
        finish()
    }

    private fun showCustomToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 200) // Pindahkan ke atas dengan offset vertikal 200px
        toast.show()
    }

    companion object {
        const val  PICK_IMAGE_REQUEST_CODE = 1001
    }

}