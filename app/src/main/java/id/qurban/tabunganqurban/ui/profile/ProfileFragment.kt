package id.qurban.tabunganqurban.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.FragmentProfileBinding
import id.qurban.tabunganqurban.ui.auth.login.LoginActivity
import id.qurban.tabunganqurban.ui.profile.kelola.KelolaAkunActivity
import id.qurban.tabunganqurban.utils.FormatHelper.toCamelCase
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuListener()
        observeUser()
        logoutListener()
    }

    private fun menuListener() {
        binding.apply {
            cardKelolaAkun.setOnClickListener {
                Intent(requireContext(), KelolaAkunActivity::class.java).also {
                    startActivity(it)
                }
            }
            cardPrivacy.setOnClickListener {  }
            cardTentang.setOnClickListener {  }
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
                            profileSemester.text = getString(R.string.profile_semester_format, it.data?.semester.toString())
                            profileProdi.text = it.data?.prodi
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}