package com.wenull.meetcrypt_students.data.firebase

import android.util.Log
import android.view.View
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.wenull.meetcrypt_students.data.auth.AuthCallBackListener
import com.wenull.meetcrypt_students.utils.models.*
import java.util.concurrent.TimeUnit

class FirebaseSource {
    private lateinit var storedVerificationId: String
    private lateinit var storedResendingToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var authCallBackListener: AuthCallBackListener
    private lateinit var fireBaseCallBackListener: FireBaseSourceCallBackListener
    
    val databaseReference = FirebaseDatabase.getInstance().getReference()

    val eventReference = FirebaseDatabase.getInstance().getReference().child("events")



    fun setAuthCallBackListener(listener: AuthCallBackListener) {
        this.authCallBackListener = listener
    }

    fun setFireBaseCallBackListener(listener: FireBaseSourceCallBackListener) {
        this.fireBaseCallBackListener = listener
    }

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted:$credential")
            authCallBackListener.onVerificationCompleted(credential)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {

            Log.w("TAG", "onVerificationFailed", e)
            authCallBackListener.onVerificationFailed(e)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            authCallBackListener.onCodeSent(verificationId, token)
            storedVerificationId = verificationId
            storedResendingToken = token

        }
    }

    fun sendVerificationCode(phone: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone.trim(),
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            callbacks
        )
    }

    fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(){task ->
            if (task.isSuccessful){
                authCallBackListener.signInSucess()
            }
            else{
                Log.i("Error", task.exception.toString())
                authCallBackListener.signInFailed(task.exception!!)
                authCallBackListener.makeProgressBarInVisible()
            }
        }
    }

    fun resendCode(phone: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            30,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            callbacks,
            storedResendingToken
        )
    }

    fun checkIfUserExists(uid: String): Boolean {

        var res = false

        val listener = object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val rawData = snapshot.value


                if(rawData is Map<*,*>) {

                    val tempData = rawData[uid]
                    if(tempData is Map<*,*>) {
                        val studentCredentials= tempData["credentials"]

                        if(studentCredentials is Map<*,*>) {
                            if (studentCredentials["uid"]?.equals(uid)!!) {

                                //store credentials data with teacher model to shared prefrence
                                val studentCredentials = StudentCredentials(
                                    uid = uid,
                                    phoneNumber = mAuth?.currentUser?.phoneNumber.toString(),
                                    username = studentCredentials["username"].toString(),
                                    classStudying = studentCredentials["classStudying"].toString().toInt()
                                    )

                                println("hello brother")
                                authCallBackListener.storeCredentials(teacherCredentials = studentCredentials)
                                authCallBackListener.makeProgressBarInVisible()
                                authCallBackListener.redirectToMainActivity()


                            }
                        }
                    } else {
                        //Go-to login fragment
                        authCallBackListener.makeProgressBarInVisible()
                        authCallBackListener.redirectToCredentialsFrag()
                    }

                }
                else {
                   // go to login fragment
                    authCallBackListener.makeProgressBarInVisible()
                    authCallBackListener.redirectToCredentialsFrag()
                }
            }


        }

        val databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child("students")
        databaseReference.addListenerForSingleValueEvent(listener)

        return res

    }

    fun setValueToCredentials(studentCredentials: StudentCredentials){
        databaseReference.child("users").child("students")
            .child(mAuth?.currentUser?.uid.toString())
            .child("credentials")
            .setValue(studentCredentials)
        authCallBackListener.storeCredentials(studentCredentials)
    }

    fun userList() {
        val usersListener = object : ChildEventListener {

            override fun onCancelled(error: DatabaseError) {
                fireBaseCallBackListener.onErrorInFollowerListener(error)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                val teacherData = snapshot.value
                if(teacherData is Map<*,*>) {

                    val credentialsMap = teacherData["credentials"] as Map<*,*>?
                    val teacherFollowersMap = if(teacherData["followers"] != null) teacherData["followers"] as Map<*, *> else null

                    var status: STATUS = STATUS.NOT_FOLLOWING
                    if(teacherFollowersMap != null) {
                        if (teacherFollowersMap.containsKey(mAuth.currentUser?.uid.toString())) {
                            status = STATUS.FOLLOWING
                        }
                    }

                    val teacher = Teacher(
                        uid = credentialsMap?.get("uid") as String,
                        phoneNumber = credentialsMap["phoneNumber"] as String,
                        username = credentialsMap["username"] as String,
                        classesTaught = credentialsMap["classesTaught"] as ArrayList<Int>,
                        status = status
                    )

                    fireBaseCallBackListener.onChangeInTeacherList(teacher)
                }

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val teacherData = snapshot.value
                if(teacherData is Map<*,*>) {

                    val credentialsMap = teacherData["credentials"] as Map<*,*>?
                    val teacherFollowersMap = if(teacherData["followers"] != null) teacherData["followers"] as Map<*, *> else null

                    var status: STATUS = STATUS.NOT_FOLLOWING
                    if(teacherFollowersMap != null) {
                        if (teacherFollowersMap.containsKey(mAuth.currentUser?.uid.toString())) {
                            status = STATUS.FOLLOWING
                        }
                    }

                    val teacher = Teacher(
                        uid = credentialsMap?.get("uid") as String,
                        phoneNumber = credentialsMap["phoneNumber"].toString(),
                        username = credentialsMap["username"] as String,
                        classesTaught = credentialsMap["classesTaught"] as ArrayList<Int>,
                        status = status
                    )

                    fireBaseCallBackListener.onAdditionOfATeacher(teacher)
                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

                val teacherData = snapshot.value
                if(teacherData is Map<*,*>) {

                    val credentialsMap = teacherData["credentials"] as Map<*,*>?
                    val teacherFollowersMap = if(teacherData["followers"] != null) teacherData["followers"] as Map<*, *> else null

                    var status: STATUS = STATUS.NOT_FOLLOWING
                    if(teacherFollowersMap != null) {
                        if (teacherFollowersMap.containsKey(mAuth.currentUser?.uid.toString())) {
                            status = STATUS.FOLLOWING
                        }
                    }

                    val teacher = Teacher(
                        uid = credentialsMap?.get("uid") as String,
                        phoneNumber = credentialsMap["phoneNumber"] as String,
                        username = credentialsMap["username"] as String,
                        classesTaught = credentialsMap["classesTaught"] as ArrayList<Int>,
                        status = status
                    )

                    fireBaseCallBackListener.OnRemovalOfATeacher(teacher)
                }

            }

        }
        fireBaseCallBackListener.changeVisiblityOfProgressBar(View.VISIBLE)
        val teachersReference = FirebaseDatabase.getInstance().getReference().child("users").child("teachers")
        teachersReference.addChildEventListener(usersListener)
        fireBaseCallBackListener.changeVisiblityOfProgressBar(View.INVISIBLE)

    }

    fun follow(studentCredentials: StudentCredentials, teacher: Teacher){

        val teacherFollowerReference = FirebaseDatabase.getInstance().getReference().child("users").child("teachers")
            .child(teacher.uid as String).child("followers")

        val studentFollowingReference = FirebaseDatabase.getInstance().getReference().child("users").child("students")
            .child(mAuth.currentUser?.uid.toString()).child("following")

        teacherFollowerReference.child(mAuth.currentUser?.uid.toString())
            .setValue(studentCredentials)
        studentFollowingReference.child(teacher.uid)
            .setValue(TeacherCredentials(teacher.uid, teacher.phoneNumber, teacher.username, teacher.classesTaught))
    }



    fun unfollow(studentCredentials: StudentCredentials, teacher: Teacher){

        val teacherFollowerReference = FirebaseDatabase.getInstance().getReference().child("users").child("teachers")
            .child(teacher.uid as String).child("followers")

        val studentFollowingReference = FirebaseDatabase.getInstance().getReference().child("users").child("students")
            .child(mAuth.currentUser?.uid.toString()).child("following")

        teacherFollowerReference.child(mAuth.currentUser?.uid.toString()).removeValue()
            .addOnCompleteListener {  }
        studentFollowingReference.child(teacher.uid).removeValue()
            .addOnCompleteListener {  }

    }

    fun getEvent(data: Any): Event {
        val dataTemp = data
        if (dataTemp is Map<*,*>) {
            val event = Event(
                dataTemp["eventUID"].toString(),
                dataTemp["eventName"].toString(),
                dataTemp["eventClass"].toString().toInt(),
                dataTemp["totalParticipants"].toString().toInt(),
                dataTemp["eventOrganiserUID"].toString(),
                dataTemp["eventOrganiserName"].toString(),
                dataTemp["eventOrganiserPhoneNumber"].toString(),
                dataTemp["randomNames"] as HashMap<String, String>? ?: HashMap()
            )
            Log.i("EventRandomNames", event.randomNames.toString())
            return event
        } else {
            return Event()
        }
    }

    fun updateEvent() {

        val eventListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                fireBaseCallBackListener.changeVisiblityOfProgressBar(View.VISIBLE)
                val data = snapshot.value
                Log.i("Data", data.toString())
                if(data is Map<*,*>) {
                    println("Yes map")
                    val event = getEvent(data)
                    fireBaseCallBackListener.onChangeInEventList(event)
                }
                fireBaseCallBackListener.changeVisiblityOfProgressBar(View.INVISIBLE)
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                fireBaseCallBackListener.changeVisiblityOfProgressBar(View.VISIBLE)

                val data = snapshot.value
                Log.i("Data", data.toString())
                if(data is Map<*,*>) {
                    println("Yes map")
                    val event = getEvent(data)
                    fireBaseCallBackListener.onAdditionOfEventInEventList(event)
                }
                fireBaseCallBackListener.changeVisiblityOfProgressBar(View.INVISIBLE)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                fireBaseCallBackListener.changeVisiblityOfProgressBar(View.VISIBLE)
                val data = snapshot.value
                Log.i("Data", data.toString())
                if(data is Map<*,*>) {
                    println("Yes map")
                    val event = getEvent(data)
                    fireBaseCallBackListener.onRemovalOfEventInEventList(event)
                }
                fireBaseCallBackListener.changeVisiblityOfProgressBar(View.INVISIBLE)
            }

        }

        val eventReference = FirebaseDatabase.getInstance().getReference().child("events")
        eventReference.addChildEventListener(eventListener)

    }



}