package co.com.mypt.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter1(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    private val fragments = ArrayList<Fragment>()
    private val fragmentTitle: MutableList<String> = ArrayList()


    fun add(fragment: Fragment, title: String, index: Int) {
        fragments.add(fragment)
        fragmentTitle.add(title)
        notifyItemInserted(index)
    }
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
    fun removeFragment(position: Int) {
        // Remove from adapter dataset
        fragments.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemId(position: Int): Long {
        return fragments[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return fragments.any { it.hashCode().toLong() == itemId }
    }
}