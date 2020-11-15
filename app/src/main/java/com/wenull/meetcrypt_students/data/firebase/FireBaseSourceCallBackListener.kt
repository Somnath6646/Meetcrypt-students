package com.wenull.meetcrypt_students.data.firebase

import com.google.firebase.database.DatabaseError
import com.wenull.meetcrypt_students.utils.models.Event
import com.wenull.meetcrypt_students.utils.models.Teacher
import com.wenull.meetcrypt_students.utils.models.TeacherCredentials

interface FireBaseSourceCallBackListener {
    fun onErrorInFollowerListener(error: DatabaseError)
    fun onChangeInTeacherList(teacher: Teacher)
    fun onAdditionOfATeacher(teacher: Teacher)
    fun OnRemovalOfATeacher(teacher: Teacher)

    fun changeVisiblityOfProgressBar(visiblity: Int)

    fun onChangeInEventList(event: Event)
    fun onAdditionOfEventInEventList(event: Event)
    fun onRemovalOfEventInEventList(event: Event)

}