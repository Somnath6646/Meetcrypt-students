package com.wenull.meetcrypt_students.data.main

import android.content.SharedPreferences
import android.view.View
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseError
import com.wenull.meetcrypt_students.data.firebase.FireBaseSourceCallBackListener
import com.wenull.meetcrypt_students.data.firebase.FirebaseSource
import com.wenull.meetcrypt_students.utils.helpers.Event
import com.wenull.meetcrypt_students.utils.helpers.ObjectSerializer
import com.wenull.meetcrypt_students.utils.models.STATUS
import com.wenull.meetcrypt_students.utils.models.StudentCredentials
import com.wenull.meetcrypt_students.utils.models.Teacher
import com.wenull.meetcrypt_students.utils.models.TeacherCredentials

class MainViewModel(private val firebaseSource: FirebaseSource,
                    private val sharedPreferences: SharedPreferences
) : ViewModel(), Observable,
    FireBaseSourceCallBackListener {



    private val indications = MutableLiveData<Event<String>>()
    val teacherList: MutableLiveData<ArrayList<Teacher>> =  MutableLiveData<ArrayList<Teacher>>()
    val followingMap: HashMap<String, Teacher> = HashMap()

    val eventList: MutableLiveData<ArrayList<com.wenull.meetcrypt_students.utils.models.Event>> =  MutableLiveData<ArrayList<com.wenull.meetcrypt_students.utils.models.Event>>()

    val studentCredentials: StudentCredentials = ObjectSerializer.deserialize(sharedPreferences.getString("Student'sCredential", null)) as StudentCredentials
    var progressBarVisible_Event: MutableLiveData<Int> = MutableLiveData<Int>(View.GONE)

    val message: LiveData<Event<String>>
        get() = indications


    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    init {
        firebaseSource.setFireBaseCallBackListener(this)
        teacherList.value = ArrayList()
        eventList.value = ArrayList()
        updateList()

    }

    fun updateList(){
        firebaseSource.userList()
    }

    fun updateEvent(){
        firebaseSource.updateEvent()
    }

    fun <T>MutableLiveData<ArrayList<Teacher>>.getIndex(phoneNumber: String): Int {
        var position = -1
        for (i in 0 until this.value!!.size){
            if (this.value!![i].phoneNumber.equals(phoneNumber)){
                position = i
                break
            }
        }
        return position
    }


    fun <T> MutableLiveData<ArrayList<com.wenull.meetcrypt_students.utils.models.Event>>.getIndexOf(uid: String): Int {
        var position = -1
        for (i in 0 until this.value!!.size){
            if (this.value!![i].eventUID.equals(uid)){
                position = i
                break
            }
        }
        return position
    }

    override fun onErrorInFollowerListener(error: DatabaseError) {

    }

    fun follow(teacher: Teacher){
        firebaseSource.follow(studentCredentials,teacher)
    }

    fun unfollow(teacher: Teacher){
        val studentCredentials: StudentCredentials = ObjectSerializer.deserialize(sharedPreferences.getString("Student'sCredential", null)) as StudentCredentials
        firebaseSource.unfollow(studentCredentials,teacher)
    }

    private fun doIFollowTheEventOrganiser(uid: String): Boolean{
        return followingMap.containsKey(uid)
    }

    override fun onChangeInEventList(event: com.wenull.meetcrypt_students.utils.models.Event) {
        if(doIFollowTheEventOrganiser(event.eventOrganiserUID)){
            val position =  eventList.getIndexOf<String>(event.eventUID)
            if(position != -1 && studentCredentials.classStudying == event.eventClass){
                eventList.value?.set(position, event)
                eventList.notifyObserver()
            }
        }
    }

    override fun onAdditionOfEventInEventList(event: com.wenull.meetcrypt_students.utils.models.Event) {
        if(doIFollowTheEventOrganiser(event.eventOrganiserUID)){
            val position =  eventList.getIndexOf<String>(event.eventUID)
            if(position == -1 && studentCredentials.classStudying == event.eventClass){
                eventList.value?.add(event)
                println("Event add hua hai")
                eventList.notifyObserver()
            }
        }
    }

    override fun onRemovalOfEventInEventList(event: com.wenull.meetcrypt_students.utils.models.Event) {
        if(doIFollowTheEventOrganiser(event.eventOrganiserUID)){
            val position =  eventList.getIndexOf<String>(event.eventUID)
            if(position != -1 && studentCredentials.classStudying == event.eventClass){
                eventList.value?.removeAt(position)
                eventList.notifyObserver()
            }
        }
    }

    override fun onChangeInTeacherList(teacher: Teacher) {
        indications.value =
            Event("change in teacher list")
        val position = teacherList.getIndex<String>(teacher.phoneNumber)
        
        if(position != -1) {
            teacherList.value!!.set(position, teacher)
            teacherList.notifyObserver()
            
            if(teacher.status == STATUS.FOLLOWING){
                followingMap.put(teacher.uid, teacher)
            }else{
                followingMap.remove(teacher.uid)
            }
        }
    }



    override fun onAdditionOfATeacher(teacher: Teacher) {
        indications.value =
            Event("addition of a teacher")

        println("Teacher phone number:- "+teacher.phoneNumber)
        val position = teacherList.getIndex<String>(teacher.phoneNumber)

        if(position == -1) {
            teacherList.value!!.add(teacher)
            teacherList.notifyObserver()
            updateEvent()
            if(teacher.status == STATUS.FOLLOWING){
                followingMap.put(teacher.uid, teacher)
            }else{
                followingMap.remove(teacher.uid)
            }
        }
    }

    override fun OnRemovalOfATeacher(teacher: Teacher) {
        indications.value =
            Event("removal of a teacher from list")

        val position = teacherList.getIndex<String>(teacher.phoneNumber)
        
        if(position != -1) {
            teacherList.value!!.removeAt(position)
            teacherList.notifyObserver()

            if(teacher.status == STATUS.FOLLOWING){
                followingMap.remove(teacher.uid)
            }
        }
    }

    override fun changeVisiblityOfProgressBar(visiblity: Int) {
        progressBarVisible_Event.value = visiblity
        println("aya visiblity:- $visiblity")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}