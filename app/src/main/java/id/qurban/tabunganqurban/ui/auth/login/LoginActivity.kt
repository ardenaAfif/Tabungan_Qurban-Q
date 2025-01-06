package id.qurban.tabunganqurban.ui.auth.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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
import id.qurban.tabunganqurban.ui.auth.register.RegisterActivity
import id.qurban.tabunganqurban.databinding.ActivityLoginBinding
import id.qurban.tabunganqurban.ui.MainActivity
import id.qurban.tabunganqurban.ui.admin.AdminHistoryActivity
import id.qurban.tabunganqurban.ui.history.HistoryActivity
import id.qurban.tabunganqurban.ui.home.HomeFragment
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeNavigation()
        gotoRegister()
        setupListeners()
        observeLoginStatus()
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            viewModel.navigate.collect { destination ->
                when (destination) {
                    LoginViewModel.TABUNGAN_ACTIVITY -> {
                        gotoHome()
                    }

                    LoginViewModel.ADMIN_TABUNGAN -> {
                        gotoHistory()
                    }

                    LoginViewModel.LOGIN_ACTIVITY -> {
                        // Tetap di halaman Login, tidak perlu aksi tambahan
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            loginButton.setOnClickListener {
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.loginUser(email, password)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Email dan Password harus diisi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun observeLoginStatus() {
        lifecycleScope.launch {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        gotoHome()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun gotoHome() {
        Intent(this, MainActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
            finish()
        }
    }

    private fun gotoHistory() {
        Intent(this, AdminHistoryActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
            finish()
        }
    }

    private fun showLoading() {
        binding.apply {
            progresBarRegister.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progresBarRegister.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
        }
    }

    private fun gotoRegister() {
        binding.notRegisteredText.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}