package id.qurban.tabunganqurban.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.qurban.tabunganqurban.ui.home.HomeFragment
import id.qurban.tabunganqurban.ui.profile.ProfileFragment
import java.lang.IllegalArgumentException

class ViewPagerHomeAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid Position")
        }
    }
}