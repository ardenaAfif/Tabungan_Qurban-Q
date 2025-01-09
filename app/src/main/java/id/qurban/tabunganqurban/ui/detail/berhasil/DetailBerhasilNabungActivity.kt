package id.qurban.tabunganqurban.ui.detail.berhasil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.databinding.ActivityDetailWaitingNabungBinding

class DetailBerhasilNabungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailWaitingNabungBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailWaitingNabungBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}