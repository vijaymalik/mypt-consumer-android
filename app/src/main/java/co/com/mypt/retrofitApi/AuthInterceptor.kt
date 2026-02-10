package co.com.mypt.retrofitApi

import android.content.SharedPreferences
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.Response

class AuthInterceptor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = sharedPreferences.getString("token", "")

        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}
