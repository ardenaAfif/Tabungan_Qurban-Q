package id.qurban.tabunganqurban.ui.admin.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.ActivityProfileAdminBinding
import id.qurban.tabunganqurban.ui.auth.login.LoginActivity
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileAdminBinding

    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logoutListener()

    }

    private fun logoutListener() {
        binding.btnLogout.setOnClickListener {
            // Hapus status login dari SharedPreferences
            profileViewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}