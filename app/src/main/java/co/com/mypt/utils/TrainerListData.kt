package co.com.mypt.utils

import co.com.mypt.model.TrainersModel

object TrainerListData {
    var trainerList: List<TrainersModel>? = null

    fun clear() {
        trainerList = null
    }
}