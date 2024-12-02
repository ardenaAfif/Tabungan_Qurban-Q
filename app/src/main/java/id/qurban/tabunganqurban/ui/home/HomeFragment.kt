package id.qurban.tabunganqurban.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.FragmentHomeBinding
import id.qurban.tabunganqurban.ui.nabung.NabungAmountActivity
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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