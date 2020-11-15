package com.wenull.meetcrypt_students.utils.models

import java.io.Serializable

class Event_cache(val isEventOn: Boolean,
                        val eventUID: String,
                        val eventRandomNamesMap: HashMap<String, String >,
                        val eventName: String): Serializable