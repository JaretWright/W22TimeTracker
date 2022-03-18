package com.constantlearningdad.w22timetracker

import com.google.firebase.Timestamp

class TimeRecord (
    var activity: String? = null,
    var startTime : Timestamp? = null,
    var endTime : Timestamp? = null
        ){

    /**
     * This method will ensure that the TimeRecord has both a startTime and an endTime,
     * then calculate the amount of minutes between the start and end times
     */
    fun getDuration() : Long
    {
        if (startTime != null && endTime != null){
            val difference = endTime!!.toDate().time - startTime!!.toDate().time
            return difference/1000/60  //convert from 1/1000 of a second to minutes
        }
        return 0
    }
}