package id.qurban.tabunganqurban.ui.home

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
import id.qurban.tabunganqurban.databinding.FragmentHomeBinding
import id.qurban.tabunganqurban.ui.nabung.NabungAmountActivity
import id.qurban.tabunganqurban.ui.profile.ProfileViewModel
import id.qurban.tabunganqurban.utils.FormatHelper
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomingTime()
        gotoNabung()
        fetchFirstName()
    }

    private fun fetchFirstName() {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            profileViewModel.fetchUser(userId)
            profileViewModel.user.observe(viewLifecycleOwner) { user ->
                if (user != null) {

                    // Format name to CamelCase
                    val formattedName = user.first_name.toCamelCase()
                    val formattedBalance = FormatHelper.formatCurrency(user.total_tabungan.toLong().toString())

                    // Update UI dengan data pengguna
                    binding.apply{
                        welcomingFirstNameText.text = formattedName
                        amountText.text = formattedBalance
                    }

                } else {
                    Toast.makeText(context, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "User tidak ditemukan.", Toast.LENGTH_SHORT).show()
            Log.d("ProfileFragment", "User ID is null")  // User ID tidak ditemukan
        }
    }

    private fun gotoNabung() {
        binding.menuNabung.setOnClickListener {
            Intent(requireContext(), NabungAmountActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun welcomingTime() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val welcomingText = when(currentHour) {
            in 5..11 -> "Selamat Pagi,"
            in 12..14 -> "Selamat Siang,"
            in 15..17 -> "Selamat Sore,"
            else -> "Selamat Malam,"
        }
        binding.welcomingTimeText.text = welcomingText
    }

    override fun onResume() {
        super.onResume()
        welcomingTime()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}