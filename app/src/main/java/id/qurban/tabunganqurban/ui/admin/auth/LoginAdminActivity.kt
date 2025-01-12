package id.qurban.tabunganqurban.ui.admin.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.ActivityLoginAdminBinding
import id.qurban.tabunganqurban.ui.admin.AdminHistoryActivity
import id.qurban.tabunganqurban.ui.auth.login.LoginViewModel
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginAdminBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupLoginButton()
    }

    private fun setupObservers() {
        loginViewModel.login.onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    hideLoading()
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Unspecified -> Unit
            }
        }.launchIn(lifecycleScope)

        loginViewModel.navigate.onEach { destination ->
            when (destination) {
                LoginViewModel.ADMIN_TABUNGAN -> {
                    startActivity(Intent(this, AdminHistoryActivity::class.java))
                    finish()
                }
                LoginViewModel.TABUNGAN_ACTIVITY -> {
                    Toast.makeText(this, "Anda tidak memiliki akses sebagai Admin", Toast.LENGTH_SHORT).show()
                }
                LoginViewModel.LOGIN_ACTIVITY -> {
                    // Tidak perlu tindakan khusus di sini
                }
                else -> Unit
            }
        }.launchIn(lifecycleScope)
    }

    private fun setupLoginButton() {
        binding.loginAdminButton.setOnClickListener {
            val email = binding.emailEditTextAdmin.text.toString().trim()
            val password = binding.passwordEditTextAdmin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginViewModel.loginUser(email, password)
        }
    }

    private fun showLoading() {
        binding.progresBarRegister.visibility = View.VISIBLE
        binding.loginAdminButton.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progresBarRegister.visibility = View.GONE
        binding.loginAdminButton.visibility = View.VISIBLE
    }
}