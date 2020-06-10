package com.trap9.codxchat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.trap9.codxchat.flagments.RecentFragment
import com.trap9.codxchat.flagments.UserFragment

class SectionPageAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment {
        when (p0) {
            0 -> return UserFragment()
            1 -> return RecentFragment()
        }
        return null!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "User"
            1 -> return "Recent"
        }
        return null!!
    }
}