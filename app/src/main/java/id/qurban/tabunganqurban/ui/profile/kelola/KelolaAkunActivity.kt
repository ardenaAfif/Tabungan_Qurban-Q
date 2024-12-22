package id.qurban.tabunganqurban.ui.profile.kelola

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.ActivityKelolaAkunBinding
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class KelolaAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelolaAkunBinding
    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customToolbar()
        observeUser()
        setupProdiSpinner()

        binding.btnUpdate.setOnClickListener {
            updateAction()
            Toast.makeText(this@KelolaAkunActivity, "Berhasil mengubah data!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProdiSpinner() {
        val programStudi = listOf("Perbandingan Madzhab", "Hukum Ekonomi Syariah")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            programStudi
        )
        binding.prodiSpinner.adapter = adapter
    }

    private fun updateAction() {
        val firstName = binding.firstNameEditText.text.toString()
        val lastName = binding.lastNameEditText.text.toString()
        val prodi = binding.prodiSpinner.selectedItem.toString()
        val semester = binding.semesterEditText.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && prodi.isNotEmpty() && semester.isNotEmpty()) {
            profileViewModel.updateProfile(
                firstName.toCamelCase(),
                lastName.toCamelCase(),
                prodi,
                semester.toInt()
            )
        } else {
            Toast.makeText(this, "...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeUser() {
        lifecycleScope.launchWhenStarted {
            profileViewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        binding.apply {
                            firstNameEditText.setText(it.data?.firstName)
                            lastNameEditText.setText(it.data?.lastName)
                            semesterEditText.setText(it.data?.semester.toString())
                            emailEditText.setText(it.data?.email)

                            // Atur nilai Spinner sesuai prodi pengguna
                            val programStudi = listOf("Perbandingan Madzhab", "Hukum Ekonomi Syariah")
                            val index = programStudi.indexOf(it.data?.prodi)
                            if (index >= 0) {
                                prodiSpinner.setSelection(index)
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@KelolaAkunActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            tvToolbarName.text = "Kelola Akun"
        }
    }
}