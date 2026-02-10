package co.com.mypt.fragments.repository

import co.com.mypt.retrofitApi.RetrofitClient

class GuestUserRepository {
    suspend fun getUsers(token: String) =
        RetrofitClient.api.getUsers(token)
    suspend fun getTrainerList(token: String,params: Map<String, String>) =
        RetrofitClient.api.getTrainerList(token,params)

    suspend fun getContent(token: String) =
        RetrofitClient.api.getContent(token)


}