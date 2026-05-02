package co.com.mypt.retrofitApi

import co.com.mypt.Api.ApiURL
import co.com.mypt.model.HomeBannersResponse
import co.com.mypt.model.GetStoriesList
import co.com.mypt.model.HomeContent
import co.com.mypt.model.TrainerListModelX
import co.com.mypt.model.TrainerStudiosResponse
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ApiService {
    @GET("get-stories?page=1")
    suspend fun getStories(@Header("Authorization") token: String): Response<GetStoriesList>

    @FormUrlEncoded
    @POST("get-trainers")
    suspend fun getTrainerList(@Header("Authorization") token: String,@FieldMap params: Map<String, String>): Response<TrainerListModelX>

    @GET("get-contents")
    suspend fun getContent(@Header("Authorization") token: String): Response<HomeContent>

    @GET("get-banners")
    suspend fun getBanners(@Header("Authorization") token: String): Response<HomeBannersResponse>

    @GET(ApiURL.getAllGymTrainers)
    suspend fun getAllGymTrainerList(@Header("Authorization") token: String,@QueryMap params: Map<String, String>): Response<TrainerListModelX>

    @GET(ApiURL.getTrainersStudios)
    suspend fun getTrainersStudios(@Header("Authorization") token: String,@QueryMap params: Map<String, String>): Response<TrainerStudiosResponse>

}