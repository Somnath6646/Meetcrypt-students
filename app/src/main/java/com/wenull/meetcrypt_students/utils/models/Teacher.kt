package com.wenull.meetcrypt_students.utils.models

import java.io.Serializable

class Teacher(val uid: String, val phoneNumber: String, val username: String, val classesTaught: ArrayList<Int>, val status: STATUS):
    Serializable {

}

enum class STATUS{
    FOLLOWING, NOT_FOLLOWING
}