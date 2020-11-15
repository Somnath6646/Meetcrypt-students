package com.wenull.meetcrypt_students.ui.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wenull.meetcrypt_students.ui.fragments.main.EventFragment
import com.wenull.meetcrypt_students.ui.fragments.main.TeacherListFragment


class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                EventFragment()
            }
            1 -> {
                TeacherListFragment()
            }
            else -> EventFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }


}