package co.com.mypt.fragments.repository

import co.com.mypt.retrofitApi.RetrofitClient

class GuestUserRepository {
    suspend fun getStories(token: String) =
        RetrofitClient.api.getStories(token)
    suspend fun getTrainerList(token: String,params: Map<String, String>) =
        RetrofitClient.api.getTrainerList(token,params)

    suspend fun getTrainerTags(token: String,params: Map<String, String>) =
        RetrofitClient.api.getTrainerList(token,params)

    suspend fun getContent(token: String) =
        RetrofitClient.api.getContent(token)

    suspend fun getBanners(token: String) =
        RetrofitClient.api.getBanners(token)

    suspend fun getAllGymTrainerList(token: String,params: Map<String, String>) =
        RetrofitClient.api.getAllGymTrainerList(token,params)

    suspend fun getTrainersStudios(token: String,params: Map<String, String>) =
        RetrofitClient.api.getTrainersStudios(token,params)

}