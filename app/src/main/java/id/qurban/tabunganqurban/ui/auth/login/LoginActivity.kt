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
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.data.UserResponse
import id.qurban.tabunganqurban.ui.auth.register.RegisterActivity
import id.qurban.tabunganqurban.databinding.ActivityLoginBinding
import id.qurban.tabunganqurban.ui.MainActivity
import id.qurban.tabunganqurban.ui.home.HomeFragment

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            gotoHome()
        }

        gotoRegister()

        setupListeners()
        observeLoginStatus()

    }

    private fun setupListeners() {
        binding.apply{
            loginButton.setOnClickListener {
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    loginViewModel.loginUser(email, password)
                } else {
                    Toast.makeText(this@LoginActivity, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.loginStatus.observe(this) { response ->
            when (response) {
                is UserResponse.Loading -> {
                    binding.progresBarRegister.visibility = View.VISIBLE
                    binding.loginButton.visibility = View.GONE
                }
                is UserResponse.Success -> {
                    binding.progresBarRegister.visibility = View.VISIBLE
                    binding.loginButton.visibility = View.GONE
                    sharedPreferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .apply()
                    gotoHome()
                }
                is UserResponse.Error -> {
                    binding.progresBarRegister.visibility = View.GONE
                    binding.loginButton.visibility = View.VISIBLE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun gotoHome() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
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