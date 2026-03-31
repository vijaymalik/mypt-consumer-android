package co.com.mypt.model

import co.com.mypt.model.TrainerListModelX.Data.Trainer.Tag
import org.json.JSONArray

class TrainersModel {

    lateinit var canBook: String
    lateinit var canMembership: String
    lateinit var is_verified: String
    lateinit var tag: String
    lateinit var timing: String
    lateinit var location: String
    lateinit var activity: JSONArray
    lateinit var profile: String
    lateinit var averageRating: String
    lateinit var noOfRating: String
    lateinit var slot: String
    lateinit var distance: String
    lateinit var id: String
    lateinit var name: String
    lateinit var studio_id: String
    lateinit var tags: List<Tag?>
     var is_group: Boolean?=null
     var isPackage: Boolean?=null
     var trainWithMe: String?=null

}
