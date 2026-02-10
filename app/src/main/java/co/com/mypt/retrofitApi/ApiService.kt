package co.com.mypt.retrofitApi

import co.com.mypt.model.GetStoriesList
import co.com.mypt.model.TrainerListModelX
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("get-stories?page=1")
    suspend fun getUsers(@Header("Authorization") token: String): Response<GetStoriesList>

    @FormUrlEncoded
    @POST("get-trainers")
    suspend fun getTrainerList(@Header("Authorization") token: String,@FieldMap params: Map<String, String>): Response<TrainerListModelX>
}