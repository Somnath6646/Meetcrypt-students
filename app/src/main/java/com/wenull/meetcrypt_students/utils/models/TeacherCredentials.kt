package com.wenull.meetcrypt_students.utils.models

import java.io.Serializable

class TeacherCredentials(val uid: String, val phoneNumber: String, val username: String, val classesTaught: ArrayList<Int>):
    Serializable {

}