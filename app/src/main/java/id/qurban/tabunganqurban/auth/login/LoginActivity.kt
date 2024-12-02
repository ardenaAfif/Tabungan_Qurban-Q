package id.qurban.tabunganqurban.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.auth.register.RegisterActivity
import id.qurban.tabunganqurban.databinding.ActivityLoginBinding
import id.qurban.tabunganqurban.ui.MainActivity
import id.qurban.tabunganqurban.ui.home.HomeFragment

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gotoRegister()
        gotoHome()
    }

    private fun gotoHome() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun gotoRegister() {
        binding.notRegisteredText.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}