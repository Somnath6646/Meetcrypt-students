package com.wenull.meetcrypt_students.ui.fragments.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.main.MainViewModel
import com.wenull.meetcrypt_students.databinding.FragmentTeacherListBinding
import com.wenull.meetcrypt_students.ui.activities.MainActivity
import com.wenull.meetcrypt_students.ui.adapter.TeacherListAdapter
import com.wenull.meetcrypt_students.ui.base.BaseFragment
import com.wenull.meetcrypt_students.utils.helpers.SearchCallBacks

class TeacherListFragment : BaseFragment<FragmentTeacherListBinding, MainViewModel>(), SearchCallBacks {

    private lateinit var adapter: TeacherListAdapter

    override fun getFragmentView(): Int = R.layout.fragment_teacher_list

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        adapter = TeacherListAdapter(viewModel)

        (activity as MainActivity).setSearchCallBackFollower(this)

        setUpRecyclerView()
    }


    fun setUpRecyclerView(){
        binding.teacherRecyclerView.adapter = adapter
        binding.teacherRecyclerView.layoutManager = LinearLayoutManager(context)
        displayTeacherList()
    }

    fun displayTeacherList(){
        viewModel.teacherList.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
    }

    override fun searchInEventFragment(newText: String) {

    }

    override fun searchInFollowerFragment(newText: String) {
        adapter.filter.filter(newText)
    }
}