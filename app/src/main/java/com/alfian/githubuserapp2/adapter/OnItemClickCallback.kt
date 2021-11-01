package com.alfian.githubuserapp2.adapter

import com.alfian.githubuserapp2.datasource.UserResponse

interface OnItemClickCallback {
    fun onItemClicked(user: UserResponse)
}