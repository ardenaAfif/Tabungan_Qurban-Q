package id.qurban.tabunganqurban.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.FragmentProfileBinding
import id.qurban.tabunganqurban.ui.auth.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileSetup()
        logoutListener()
    }

    private fun profileSetup() {
        // Ambil userId dari SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            Log.d("ProfileFragment", "User ID retrieved: $userId")  // Pastikan user_id benar

            profileViewModel.fetchUser(userId)
            profileViewModel.user.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    Log.d("ProfileFragment", "User data received: $user")  // Pastikan data diterima

                    // Update UI dengan data pengguna
                    binding.profileName.text = "${user.first_name} ${user.last_name}"
                    binding.profileEmail.text = user.email
                    binding.profileProdi.text = user.prodi
                    binding.profileSemester.text = " / ${user.semester}"
                } else {
                    Toast.makeText(context, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "User tidak ditemukan.", Toast.LENGTH_SHORT).show()
            Log.d("ProfileFragment", "User ID is null")  // User ID tidak ditemukan
        }
    }

    private fun logoutListener() {
        binding.btnLogout.setOnClickListener {
            // Hapus status login dari SharedPreferences
            val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .clear()
                .apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}