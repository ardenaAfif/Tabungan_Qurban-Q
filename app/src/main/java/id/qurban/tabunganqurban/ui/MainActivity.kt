package id.qurban.tabunganqurban.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.adapter.ViewPagerHomeAdapter
import id.qurban.tabunganqurban.data.Transaction
import id.qurban.tabunganqurban.databinding.ActivityMainBinding
import id.qurban.tabunganqurban.ui.detail.berhasil.DetailBerhasilNabungActivity
import id.qurban.tabunganqurban.ui.nabung.NabungVM
import id.qurban.tabunganqurban.utils.Resource
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2
    private val nabungViewModel: NabungVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPagerHome
        bottomNavigationView = binding.bottomNavViewHome

        // Setup ViewPager dengan Adapter
        val adapter = ViewPagerHomeAdapter(this)
        viewPager.adapter = adapter

        // Sinkronisasi ViewPager dengan BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> viewPager.currentItem = 0
                R.id.menu_about -> viewPager.currentItem = 1
                else -> false
            }
            true
        }

        // Sinkronisasi ViewPager Swipe dengan BottomNavigation
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNavigationView.selectedItemId = R.id.menu_home
                    1 -> bottomNavigationView.selectedItemId = R.id.menu_about
                }
            }
        })

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestNotificationPermission()
//        } else {
//            observeTransactionStatus()
//        }
    }

    private fun observeTransactionStatus() {
        // Anda bisa memantau semua transaksi atau hanya transaksi tertentu
        // Memantau semua transaksi
        lifecycleScope.launchWhenStarted {
            nabungViewModel.allTransactions.collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Menampilkan indikator loading jika diperlukan
                    }

                    is Resource.Success -> {
                        result.data?.forEach { transaction ->
                            if (transaction.status == "Berhasil" && !isNotificationShown(transaction.transactionId)) {
                                showNotification(transaction)
                                markNotificationShown(transaction.transactionId)
                            }
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Gagal memperbarui data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun isNotificationShown(transactionId: String): Boolean {
        // Implementasikan logika untuk memeriksa apakah notifikasi sudah ditampilkan
        // Menggunakan SharedPreferences
        val sharedPreferences = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(transactionId, false)
    }

    private fun markNotificationShown(transactionId: String) {
        // Implementasi logika untuk menandai notifikasi sudah ditampilkan
        val sharedPreferences = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(transactionId, true)
        editor.apply()
    }

    private fun showNotification(transaction: Transaction) {
        val channelId = "transaction_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Membuat channel notifikasi untuk Android Oreo ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Transaction Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, DetailBerhasilNabungActivity::class.java).apply {
            putExtra("transaction", transaction)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_tabungan_qurban)
            .setContentTitle("Status Transaksi Diperbarui")
            .setContentText("Status transaksi Anda telah berubah menjadi Berhasil.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(transaction.transactionId.hashCode(), builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan
            } else {
                // Izin ditolak
                Toast.makeText(
                    this,
                    "Izin notifikasi diperlukan untuk menerima notifikasi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}