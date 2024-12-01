package id.qurban.tabunganqurban

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.qurban.tabunganqurban.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
    }
}