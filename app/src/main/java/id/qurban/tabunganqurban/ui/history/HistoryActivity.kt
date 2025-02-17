package id.qurban.tabunganqurban.ui.history

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.adapter.HistoryViewPager
import id.qurban.tabunganqurban.databinding.ActivityHistoryBinding
import id.qurban.tabunganqurban.ui.MainActivity

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customToolbar()
        setupTabLayoutWithViewPager()
    }

    private fun setupTabLayoutWithViewPager() {
        val adapter = HistoryViewPager(this)
        binding.viewPagerHistory.adapter = adapter

        val tabColors = listOf(
            R.color.primary, // Semua
            R.color.purple, // Pending
            R.color.orange, // Menunggu Konfirmasi
            R.color.primary, // Berhasil
            R.color.red, // Dibatalkan
        )

        TabLayoutMediator(binding.tabLayoutHistory, binding.viewPagerHistory) { tab, position ->
            when (position) {
                0 -> tab.text = "Semua"
                1 -> tab.text = "Menunggu Konfirmasi"
                2 -> tab.text = "Mengecek"
                3 -> tab.text = "Berhasil"
                4 -> tab.text = "Dibatalkan"
                else -> "Tab $position"
            }
        }.attach()

        // Ubah warna indikator dan teks saat tab dipilih
        binding.tabLayoutHistory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: return
                val selectedColor = getColor(tabColors[position])
                val whiteColor = getColor(R.color.white)

                // Ubah warna indikator dan teks
                binding.tabLayoutHistory.setSelectedTabIndicatorColor(selectedColor)
                tab.view.apply {
                    (this.getChildAt(1) as? TextView)?.apply {
                        setTextColor(whiteColor)
                        setPadding(20, 0, 20, 0) // Padding teks aktif
                    }
                }
                binding.tabLayoutHistory.setTabTextColors(
                    getColor(R.color.black), // Warna teks tidak terpilih
                    whiteColor // Warna teks terpilih
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.apply {
                    setBackgroundResource(android.R.color.transparent) // Reset background
                    (this.getChildAt(1) as? TextView)?.apply {
                        setTextColor(getColor(R.color.black)) // Reset warna teks
                        setPadding(0, 0, 0, 0) // Reset padding
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun customToolbar() {
        binding.toolbar.apply {
            navBack.setOnClickListener {
                val intent = Intent(this@HistoryActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            tvToolbarName.text = "Riwayat Menabung"

            infoStatus.visibility = View.VISIBLE
            infoStatus.setOnClickListener {
                showInfoDialog()
            }
        }
    }

    private fun showInfoDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_info_status)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnTutup: TextView = dialog.findViewById(R.id.btnTutup)

        btnTutup.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}