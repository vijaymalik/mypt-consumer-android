package co.com.mypt.model

import org.json.JSONArray

class NearByGymModel {

    lateinit var description: String
    lateinit var tag: String
    var activity: JSONArray? = null
    lateinit var timing: String
    lateinit var location: String
    lateinit var profile: String
    lateinit var averageRating: String
    lateinit var noOfRating: String
    lateinit var slot: String
    lateinit var distance: String
    lateinit var id: String
    lateinit var name: String
}
