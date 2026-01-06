package co.com.mypt.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    private val fragments: MutableList<Fragment> = ArrayList<Fragment>()
    private val fragmentTitle: MutableList<String> = ArrayList()

    fun add(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitle.add(title)
    }
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun replaceFragmentAt(position: Int, fragment: Fragment) {
        fragments[position] = fragment
        notifyItemChanged(position)
    }
    /*override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }*/
}