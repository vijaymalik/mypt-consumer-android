package co.com.mypt.retrofitApi

import co.com.mypt.model.GetStoriesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("get-stories?page=1")
    suspend fun getUsers(@Header("Authorization") token: String): Response<GetStoriesList>
}