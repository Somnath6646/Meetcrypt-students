package com.wenull.meetcrypt_students.ui.fragments.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.main.MainViewModel
import com.wenull.meetcrypt_students.databinding.FragmentEventBinding
import com.wenull.meetcrypt_students.ui.activities.MainActivity
import com.wenull.meetcrypt_students.ui.adapter.EventListAdapter
import com.wenull.meetcrypt_students.ui.base.BaseFragment
import com.wenull.meetcrypt_students.utils.helpers.ObjectSerializer
import com.wenull.meetcrypt_students.utils.helpers.SearchCallBacks
import com.wenull.meetcrypt_students.utils.models.StudentCredentials

class EventFragment : BaseFragment<FragmentEventBinding, MainViewModel>(), SearchCallBacks{


    private lateinit var adapter: EventListAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var avdProgress: AnimatedVectorDrawableCompat



    override fun getFragmentView(): Int = R.layout.fragment_event

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initializeAvd()

        (activity as MainActivity).setSearchCallBackEvent(this)

        binding.image.visibility = View.VISIBLE
        viewModel.progressBarVisible_Event.observe(viewLifecycleOwner, Observer {
            binding.image.visibility = it
        })



        sharedPreferences = requireActivity().getSharedPreferences("com.example.meetcryptforstudents", Context.MODE_PRIVATE)
        val studentCredentials: StudentCredentials = ObjectSerializer.deserialize(sharedPreferences.getString("Student'sCredential", null)) as StudentCredentials

        adapter = EventListAdapter(requireContext(), studentCredentials)
        setUpRecyclerView()

    }

    private fun setUpRecyclerView(){
        binding.eventRecyclerView.adapter = adapter
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(context)
        displayEventList()
    }

    private fun displayEventList(){
        viewModel.eventList.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
    }

    var action = Runnable { repeatAnimation() }

    private fun initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd)!!
        binding.image.setBackground(avdProgress)
        repeatAnimation()
    }

    private fun repeatAnimation() {
        avdProgress.start()
        binding.image.postDelayed(action, 1000) // Will repeat animation in every 1 second
    }


    override fun searchInEventFragment(newText: String) {
        adapter.filter.filter(newText)
    }

    override fun searchInFollowerFragment(newText: String) {

    }
}