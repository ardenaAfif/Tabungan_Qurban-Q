package id.qurban.tabunganqurban.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.qurban.tabunganqurban.R
import id.qurban.tabunganqurban.adapter.ViewPagerHomeAdapter
import id.qurban.tabunganqurban.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2

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

    }
}