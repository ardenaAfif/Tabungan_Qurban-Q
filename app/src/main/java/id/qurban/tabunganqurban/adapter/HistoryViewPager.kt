package id.qurban.tabunganqurban.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.qurban.tabunganqurban.ui.history.all.AllHistoryFragment
import id.qurban.tabunganqurban.ui.history.berhasil.BerhasilHistoryFragment
import id.qurban.tabunganqurban.ui.history.dibatalkan.BatalHistoryFragment
import id.qurban.tabunganqurban.ui.history.mengecek.MengecekHistoryFragment
import id.qurban.tabunganqurban.ui.history.waiting.WaitingHistoryFragment

class HistoryViewPager(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        AllHistoryFragment(),
        WaitingHistoryFragment(),
        MengecekHistoryFragment(),
        BerhasilHistoryFragment(),
        BatalHistoryFragment(),
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}