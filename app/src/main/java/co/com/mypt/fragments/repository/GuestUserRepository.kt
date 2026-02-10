package co.com.mypt.fragments.repository

import co.com.mypt.retrofitApi.RetrofitClient

class GuestUserRepository {
    suspend fun getUsers(token: String) =
        RetrofitClient.api.getUsers(token)
}