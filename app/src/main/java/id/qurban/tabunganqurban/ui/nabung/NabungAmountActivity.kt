package id.qurban.tabunganqurban.ui.nabung

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.ActivityNabungAmountBinding
import id.qurban.tabunganqurban.databinding.CustomKeyboardBinding
import id.qurban.tabunganqurban.utils.FormatHelper.formatCurrency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class NabungAmountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNabungAmountBinding
    private lateinit var customKeyboardBinding: CustomKeyboardBinding
    private var inputConnection: InputConnection? = null
    private lateinit var keyboardBottomSheet: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNabungAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inflate custom keyboard layout
        customKeyboardBinding = CustomKeyboardBinding.inflate(layoutInflater)
        keyboardBottomSheet = BottomSheetDialog(this).apply {
            setContentView(customKeyboardBinding.root)
            setCancelable(true)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            setOnDismissListener {
                binding.amountEditText.clearFocus()
            }
        }

        // Disable default keyboard
        disableDefaultKeyboard(binding.amountEditText)

        // Set initial state for amountEditText and btnLanjut
        binding.apply {
            amountEditText.setText("Rp 0")
            amountEditText.setSelection(4) // Posisi setelah "Rp "
            btnLanjut.isEnabled = false
            btnLanjut.setBackgroundColor(ContextCompat.getColor(this@NabungAmountActivity, R.color.darker_grey))
            btnLanjut.setTextColor(ContextCompat.getColor(this@NabungAmountActivity, R.color.white))
        }

        binding.amountEditText.requestFocus()
        inputConnection = binding.amountEditText.onCreateInputConnection(EditorInfo())
        keyboardBottomSheet.show()

        // Handle focus change for amountEditText to hide keyboard
        binding.amountEditText.setOnTouchListener { _, _ ->
            if (!keyboardBottomSheet.isShowing) {
                keyboardBottomSheet.show()
            }

            if (!binding.amountEditText.hasFocus()) {
                binding.amountEditText.requestFocus()
            }

            inputConnection = binding.amountEditText.onCreateInputConnection(EditorInfo())
            false // Tetap memproses event sentuhan untuk fokus
        }

        setupCustomKeyboard()
        setupTextWatcher()
        setupTemplateAmountSpinner()
        customToolbar()
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            tvToolbarName.text = "Nabung Sekarang"
        }
    }

    private fun setupTemplateAmountSpinner() {
        binding.apply {
            template10k.setOnClickListener { setAmount("10000") }
            template20k.setOnClickListener { setAmount("20000") }
            template50k.setOnClickListener { setAmount("50000") }
            template100k.setOnClickListener { setAmount("100000") }
        }
    }


    private fun setupCustomKeyboard() {
        customKeyboardBinding.apply {

            // Handle button clicks to insert text into EditText
            val numberClickListener = View.OnClickListener { v ->
                val button = v as Button
                val buttonText = button.text.toString()
                inputConnection?.commitText(buttonText, 1)
            }

            key1.setOnClickListener(numberClickListener)
            key2.setOnClickListener(numberClickListener)
            key3.setOnClickListener(numberClickListener)
            key4.setOnClickListener(numberClickListener)
            key5.setOnClickListener(numberClickListener)
            key6.setOnClickListener(numberClickListener)
            key7.setOnClickListener(numberClickListener)
            key8.setOnClickListener(numberClickListener)
            key9.setOnClickListener(numberClickListener)
            key0.setOnClickListener(numberClickListener)
            key000.setOnClickListener(numberClickListener)


            // Handle delete button functionality
            keyDelete.setOnClickListener {
                inputConnection?.deleteSurroundingText(1, 0)
            }

            // Handle selesai button (if needed)
            btnSelesai.setOnClickListener {
                // Hide the custom keyboard or take any desired action
                binding.amountEditText.clearFocus()

                // Menutup BottomSheetDialog
                if (keyboardBottomSheet.isShowing) {
                    keyboardBottomSheet.dismiss()
                }
            }
        }
    }

    private fun setupTextWatcher() {
        val etAmountNabung = binding.amountEditText

        etAmountNabung.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                etAmountNabung.removeTextChangedListener(this)

                val text = s.toString().replace(".", "").replace("Rp ", "")
                val amount = text.toLongOrNull() ?: 0L

                if (text.isEmpty() || text == "0") {
                    etAmountNabung.setText("Rp 0")
                    etAmountNabung.setSelection(4) // Posisi setelah "Rp "
                } else {
                    etAmountNabung.setText(formatCurrency(text))
                    etAmountNabung.setSelection(etAmountNabung.text.length)
                }

                // Kondisi btnLanjut
                binding.apply {
                    if (amount < 10000L) {
                        btnLanjut.isEnabled = false
                        btnLanjut.setBackgroundColor(ContextCompat.getColor(this@NabungAmountActivity, R.color.darker_grey)) // Warna utama
                        btnLanjut.setTextColor(ContextCompat.getColor(this@NabungAmountActivity, R.color.white)) // Warna teks putih
                        tvWarning.visibility = View.VISIBLE
                    } else {
                        btnLanjut.isEnabled = true
                        btnLanjut.setBackgroundColor(ContextCompat.getColor(this@NabungAmountActivity, R.color.primary)) // Warna utama
                        btnLanjut.setTextColor(ContextCompat.getColor(this@NabungAmountActivity, R.color.white)) // Warna teks putih
                        binding.tvWarning.visibility = View.GONE
                        btnLanjut.setOnClickListener {
                            val intent = Intent(this@NabungAmountActivity, DetailNabungActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
                etAmountNabung.addTextChangedListener(this)
            }
        })
    }

    // Function to update the amountEditText with selected template value
    private fun setAmount(amount: String) {
        val formattedAmount = formatCurrency(amount)
        binding.amountEditText.setText(formattedAmount)
        binding.amountEditText.setSelection(formattedAmount.length) // Move the cursor to the end
    }

    private fun disableDefaultKeyboard(editText: EditText) {
        editText.showSoftInputOnFocus = false // Hanya untuk API 21+ (Lollipop ke atas)

        // Untuk kompatibilitas dengan API di bawah 21
        try {
            val method = TextView::class.java.getMethod(
                "setShowSoftInputOnFocus",
                Boolean::class.javaPrimitiveType
            )
            method.invoke(editText, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}