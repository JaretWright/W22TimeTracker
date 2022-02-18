package com.constantlearningdad.w22timetracker

import com.google.firebase.Timestamp

class TimeRecord (
    var activity: String? = null,
    var startTime : Timestamp? = null,
    var endTime : Timestamp? = null
        )