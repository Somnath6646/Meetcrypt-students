package com.wenull.meetcrypt_students.utils.models

import java.io.Serializable

class StudentCredentials(val uid: String, val phoneNumber: String, val username: String, val classStudying: Int): Serializable {
}