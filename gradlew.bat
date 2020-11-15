package com.wenull.meetcrypt.data.main

import android.content.SharedPreferences
import android.util.Log
import android.widget.RadioGroup
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import com.wenull.meetcrypt.data.firebase.FireBaseSourceCallBackListener
import com.wenull.meetcrypt.data.firebase.FirebaseSource
import com.wenull.meetcrypt.utils.helpers.Event
import com.wenull.meetcrypt.utils.ObjectSerializer
import com.wenull.meetcrypt.utils.RandomNames
import com.wenull.meetcrypt.utils.models.Event_cache
import com.wenull.meetcrypt.utils.models.Follower
import com.wenull.meetcrypt.utils.models.TeacherCredentials


class MainViewModel(private val firebaseSource: FirebaseSource,
                    private val sharedPreferences: SharedPreferences
) : ViewModel(), Observable,
    FireBaseSourceCallBackListener {



    @Bindable
    val eventName = MutableLiveData<String>()

    @Bindable
    val checkedClass = MutableLiveData<Int>()

    val followerList: MutableLiveData<ArrayList<Follower>> =  MutableLiveData<ArrayList<Follower>>()

    val isEventOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    var participantList: ArrayList<Follower> = ArrayList()

    var randomNames: HashMap<String, String> = HashMap<String, String>()


    val teacherCredentials: TeacherCredentials = ObjectSerializer.deserialize(sharedPreferences.getString("Teacher'sCredential", "")) as TeacherCredentials


    private val indications = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = indications

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    init {
        firebaseSource.setFireBaseCallBackListener(this)
        followerList.value = ArrayList()
    }

    fun updateList(){
        firebaseSource.followerList()
    }

    fun checkForEvent(){
        firebaseSource.getEventOnRestart()
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    f