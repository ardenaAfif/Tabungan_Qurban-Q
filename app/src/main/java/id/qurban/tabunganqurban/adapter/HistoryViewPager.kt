package id.qurban.tabunganqurban.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.qurban.tabunganqurban.ui.history.all.AllHistoryFragment
import id.qurban.tabunganqurban.ui.history.berhasil.BerhasilHistoryFragment
import id.qurban.tabunganqurban.ui.history.pending.PendingHistoryFragment
import id.qurban.tabunganqurban.ui.history.waiting.WaitingHistoryFragment

class HistoryViewPager(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        AllHistoryFragment(),
        PendingHistoryFragment(),
        WaitingHistoryFragment(),
        BerhasilHistoryFragment(),
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}