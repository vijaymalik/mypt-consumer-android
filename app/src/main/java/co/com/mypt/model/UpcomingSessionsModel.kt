package co.com.mypt.model

import org.json.JSONArray

class UpcomingSessionsModel {

    lateinit var selected_slot: String
    lateinit var distance: String
    lateinit var trainer_image: String
    var workout_focus: JSONArray? = null
    lateinit var is_Trainer: String
    lateinit var msg: String
    lateinit var is_reschedule: String
    lateinit var location: String
    lateinit var trainer: String
    lateinit var duration: String
    lateinit var session_type: String
    lateinit var timing: String
    lateinit var type: String
    lateinit var id: String

}
