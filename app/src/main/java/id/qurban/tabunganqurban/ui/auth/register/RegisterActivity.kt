package id.qurban.tabunganqurban.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.data.UserResponse
import id.qurban.tabunganqurban.databinding.ActivityRegisterBinding
import id.qurban.tabunganqurban.ui.auth.login.LoginActivity
import id.qurban.tabunganqurban.utils.PasswordHasher
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()  // ViewModel untuk registrasi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gotoLogin()
        setupListeners()
        observeRegistrationStatus()
        validationForm()
    }

    private fun validationForm(): Boolean {
        binding.apply {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val prodi = prodiEditText.text.toString().trim()
            val semester = semesterEditText.text.toString().trim()

            // Cek apakah semua field terisi
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                firstName.isEmpty() || lastName.isEmpty() || prodi.isEmpty() || semester.isEmpty()
            ) {
                Toast.makeText(this@RegisterActivity, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return false
            }

            // Cek apakah password dan confirmPassword sama
            if (password != confirmPassword) {
                Toast.makeText(this@RegisterActivity, "Password dan Konfirmasi Password tidak cocok!", Toast.LENGTH_SHORT).show()
                return false
            }

            // Jika semua validasi lolos
            return true
        }
    }


    private fun observeRegistrationStatus() {
        // Mengamati perubahan status registrasi
        registerViewModel.registrationStatus.observe(this, Observer { userResponse ->
            when (userResponse) {
                is UserResponse.Loading -> {
                    // Menampilkan loading jika sedang memproses
                    binding.progresBarRegister.visibility = View.VISIBLE
                    binding.registerButton.visibility = View.GONE
                }

                is UserResponse.Success -> {
                    // Menyembunyikan loading dan menampilkan pesan sukses
                    binding.registerButton.visibility = View.GONE
                    binding.progresBarRegister.visibility = View.VISIBLE
                    Toast.makeText(
                        this,
                        "Selamat! Anda berhasil membuat akun tabungan",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Menavigasi ke LoginActivity setelah registrasi berhasil
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

                is UserResponse.Error -> {
                    // Menyembunyikan loading dan menampilkan pesan error
                    binding.registerButton.visibility = View.VISIBLE
                    binding.progresBarRegister.visibility = View.GONE
                    Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupListeners() {
        binding.apply {
            registerButton.setOnClickListener {

                if (!validationForm()) {
                    return@setOnClickListener
                }

                // Hash password sebelum disimpan ke database
                val hashedPassword = PasswordHasher.hashPassword(passwordEditText.text.toString().trim())

                // Membuat objek User untuk dikirim ke ViewModel
                val user = User(
                    user_id = UUID.randomUUID().toString(),
                    email = emailEditText.text.toString().trim(),
                    password = hashedPassword,
                    first_name = firstNameEditText.text.toString(),
                    last_name = lastNameEditText.text.toString(),
                    prodi = prodiEditText.text.toString(),
                    semester = semesterEditText.text.toString().toIntOrNull() ?: 0,
                    total_tabungan = 0.0
                )

                // Memanggil fungsi registerUser dari ViewModel
                registerViewModel.registerUser(user)
            }
        }
    }

    private fun gotoLogin() {
        binding.alreadyRegisteredText.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}