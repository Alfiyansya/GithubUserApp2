package com.alfian.githubuserapp2.networking

import com.alfian.githubuserapp2.BuildConfig
import com.alfian.githubuserapp2.datasource.SearchResponse
import com.alfian.githubuserapp2.datasource.UserResponse
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    fun getListUsersAsync(): Deferred<ArrayList<UserResponse>>

    @GET("users/{username}")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    fun getDetailUserAsync(@Path("username") username: String): Deferred<UserResponse>

    @GET("search/users")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    fun getUserBySearch(@Query("q") username: String): Call<SearchResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    fun getListFollowers(@Path("username") username: String): Call<ArrayList<UserResponse>>

    @GET("users/{username}/following")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    fun getListFollowing(@Path("username") username: String): Call<ArrayList<UserResponse>>

    companion object {
        private const val API_TOKEN = BuildConfig.API_TOKEN
    }
}
