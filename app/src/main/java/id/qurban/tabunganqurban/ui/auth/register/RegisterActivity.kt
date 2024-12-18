package id.qurban.tabunganqurban.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.data.User
import id.qurban.tabunganqurban.databinding.ActivityRegisterBinding
import id.qurban.tabunganqurban.ui.auth.login.LoginActivity
import id.qurban.tabunganqurban.utils.RegisterValidation
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeRegistration()
        formValidation()
        registerAction()
    }

    private fun registerAction() {
        binding.apply {
            registerButton.setOnClickListener {
                val user = User(
                    emailEditText.text.toString(),
                    firstNameEditText.text.toString().trim(),
                    lastNameEditText.text.toString().trim(),
                    prodiEditText.text.toString().trim(),
                    semesterEditText.text.toString().toIntOrNull() ?: 0, // Konversi ke Int
                    0.0
                )
                val password = passwordEditText.text.toString()
                if (user != null && password.isNotEmpty()) {
                    viewModel.createAccuntWithEmailAndPassword(user, password)
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Silahkan isi dengan lengkap terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun observeRegistration() {
        // Mengamati perubahan status registrasi
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Berhasil Mendaftar",
                            Toast.LENGTH_SHORT
                        ).show()
                        gotoLogin()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(
                            this@RegisterActivity,
                            it.message,
                            Toast.LENGTH_SHORT
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun formValidation() {
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.empty is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            validation.empty.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (validation.name is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            validation.name.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (validation.prodi is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            validation.prodi.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (validation.semester is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            validation.semester.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            validation.email.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            validation.password.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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

    private fun showLoading() {
        binding.apply {
            progresBarRegister.visibility = View.VISIBLE
            registerButton.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progresBarRegister.visibility = View.GONE
            registerButton.visibility = View.VISIBLE
        }
    }
}