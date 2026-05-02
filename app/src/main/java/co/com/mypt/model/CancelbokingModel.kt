package co.com.mypt.model

import org.json.JSONArray

class CancelbokingModel {


    lateinit var is_Trainer: String
    lateinit var type: String
    var workout_focus: JSONArray? = null
    lateinit var msg: String
    lateinit var is_reschedule: String
    lateinit var location: String
    lateinit var trainer: String
    lateinit var duration: String
    lateinit var session_type: String
    lateinit var timing: String
    lateinit var id: String
    lateinit var name: String
    lateinit var booking_type: String
}
