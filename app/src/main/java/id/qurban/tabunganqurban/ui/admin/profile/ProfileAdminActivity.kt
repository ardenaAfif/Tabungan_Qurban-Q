package id.qurban.tabunganqurban.ui.admin.profile

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.ActivityProfileAdminBinding
import id.qurban.tabunganqurban.ui.auth.login.LoginActivity
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileAdminBinding

    private val profileViewModel by viewModels<ProfileViewModel>()
    private val profileAdminVM: ProfileAdminVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customToolbar()
        totalStatusSetup()
        observeUser()
        logoutListener()
        observeTransactionStatusCounts()
    }

    private fun observeTransactionStatusCounts() {
        lifecycleScope.launchWhenStarted {
            profileAdminVM.transactionStatusCounts.collectLatest { statusCounts ->
                binding.apply {
                    tvJumlahWaiting.text = statusCounts["Menunggu Konfirmasi"].toString()
                    tvJumlahCek.text = statusCounts["Mengecek"].toString()
                    tvJumlahBerhasil.text = statusCounts["Berhasil"].toString()
                }
            }
        }      }

    private fun totalStatusSetup() {
        binding.apply {
            tvJumlahWaiting.text = "0"
            tvJumlahCek.text = "0"
            tvJumlahBerhasil.text = "0"
        }
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                onBackPressed()
            }
            tvToolbarName.text = "Profil"
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
                            profileName.text = getString(
                                R.string.profile_name_format,
                                it.data?.firstName.orEmpty().toCamelCase(),
                                it.data?.lastName.orEmpty().toCamelCase())
                            profileEmail.text = it.data?.email
                            profileProdi.text = it.data?.prodi
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@ProfileAdminActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun logoutListener() {
        binding.btnLogout.setOnClickListener {
            // Hapus status login dari SharedPreferences
            profileViewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}