package com.alfian.githubuserapp2.ui.follows

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alfian.githubuserapp2.datasource.UserResponse
import com.alfian.githubuserapp2.networking.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowsViewModel(username: String) : ViewModel() {
    private val _followers = MutableLiveData<ArrayList<UserResponse>?>()
    val followers: LiveData<ArrayList<UserResponse>?> = _followers
    private val _following = MutableLiveData<ArrayList<UserResponse>?>()
    val following: LiveData<ArrayList<UserResponse>?> = _following
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isDataFailed = MutableLiveData<Boolean>()
    val isDataFailed: LiveData<Boolean> = _isDataFailed

    companion object {
        private const val TAG = "FollowersAndFollowingViewModel"
    }

    init {
        getListFollowers(username)
        getListFollowing(username)
        Log.i(TAG, "FollFragment is Created")
    }

    private fun getListFollowers(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getListFollowers(username)
        client.enqueue(object : Callback<ArrayList<UserResponse>> {
            override fun onResponse(
                call: Call<ArrayList<UserResponse>>,
                response: Response<ArrayList<UserResponse>>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _followers.postValue(responseBody)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<UserResponse>>, t: Throwable) {
                _isLoading.value = false
                _isDataFailed.value = true
                Log.e(TAG, "OnFailure: ${t.message}")
            }
        })
    }

    private fun getListFollowing(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getListFollowing(username)
        client.enqueue(object : Callback<ArrayList<UserResponse>> {
            override fun onResponse(
                call: Call<ArrayList<UserResponse>>,
                response: Response<ArrayList<UserResponse>>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _following.postValue(responseBody)
                    }
                } else {
                    _isLoading.value = false
                    _isDataFailed.value = true
                }
            }

            override fun onFailure(call: Call<ArrayList<UserResponse>>, t: Throwable) {
                _isLoading.value = false
                _isDataFailed.value = true
                Log.e(TAG, "OnFailure: ${t.message}")
            }
        })
    }
}