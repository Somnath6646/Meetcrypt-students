package com.wenull.meetcrypt_students.utils.models

import java.io.Serializable

class Event(val eventUID: String = "", // eventUID is the same as teacher's UID
            val eventName: String = "",// teacher will enter
            val eventClass: Int = 0,//-
            val totalParticipants: Int = 0,// line - 153 efrag
            val eventOrganiserUID: String = "",
            val eventOrganiserName: String = "",
            val eventOrganiserPhoneNumber: String = "",
            val randomNames: Map<String, String> = HashMap()
): Serializable
