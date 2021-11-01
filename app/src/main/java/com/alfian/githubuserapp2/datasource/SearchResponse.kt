package com.alfian.githubuserapp2.datasource

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("incomplete_result")
    val incompleteResult: Boolean,

    val items: ArrayList<UserResponse>?
)