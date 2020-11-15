package com.wenull.meetcrypt_students.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.animate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.wenull.meetcrypt_students.R
import com.wenull.meetcrypt_students.data.firebase.FirebaseSource
import com.wenull.meetcrypt_students.data.main.MainViewModel
import com.wenull.meetcrypt_students.data.main.MainViewModelFactory
import com.wenull.meetcrypt_students.databinding.ActivityMainBinding
import com.wenull.meetcrypt_students.ui.adapter.ViewPagerAdapter
import com.wenull.meetcrypt_students.utils.helpers.NetworkObserver
import com.wenull.meetcrypt_students.utils.helpers.SearchCallBacks
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var searchCallBacksEvent: SearchCallBacks
    private lateinit var searchCallBacksFollower: SearchCallBacks

    private lateinit var binding: ActivityMainBinding

    fun setSearchCallBackEvent(searchCallBack: SearchCallBacks) {
        this.searchCallBacksEvent = searchCallBack
    }

    fun setSearchCallBackFollower(searchCallBack: SearchCallBacks) {
        this.searchCallBacksFollower = searchCallBack
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedPreferences =
            this.getSharedPreferences("com.example.meetcryptforstudents", Context.MODE_PRIVATE)

        val firebaseSource = FirebaseSource()
        val factory =
            MainViewModelFactory(
                firebaseSource,
                sharedPreferences
            )
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        viewModel.message.observe(this, Observer {
            it.getContentIfNotHandled().let {
                if (it != null)
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

            }
        })

        defineLayout()
        handleNetworkChanges()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val search = menu.findItem(R.id.action_search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                if (pager.currentItem == 0) {
                    searchCallBacksEvent.searchInEventFragment(newText)
                } else {
                    searchCallBacksFollower.searchInFollowerFragment(newText)
                }

                return false
            }
        })

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                sharedPreferences.edit().clear()
                val intent = Intent(this, PhoneAuthActivity::class.java)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun createCardAdapter(): ViewPagerAdapter? {
        return ViewPagerAdapter(this)
    }

    private fun defineLayout() {
        setUpActionBar()

        pager.setAdapter(createCardAdapter())

        setUpTabLayout()
    }


    private fun setUpActionBar() {

        supportActionBar?.elevation = 0F
        //tabLayout
        this.supportActionBar?.show()
    }

    private fun setUpTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Meetings"))
        tabLayout.addTab(tabLayout.newTab().setText("Teachers"))
        TabLayoutMediator(tabLayout, pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = when (position) {
                    0 -> "Meeting"
                    1 -> "Teachers"
                    else -> "Tab"
                }
            }).attach()
    }

    private fun handleNetworkChanges() {
        NetworkObserver.getNetworkLiveData(applicationContext)
            .observe(this, androidx.lifecycle.Observer { isConnected ->
                if (!isConnected) {
                    binding.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                    binding.networkStatusLayout.apply {
                        binding.networkStatusLayout.visibility = View.VISIBLE
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorStatusNotConnected
                            )
                        )
                    }
                } else {
                    binding.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                    binding.networkStatusLayout.apply {
                        animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    binding.networkStatusLayout.visibility = View.GONE
                                }
                            })
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorStatusConnected
                            )
                        )
                    }
                }
            })
    }

}
