package com.alfian.githubuserapp2.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.alfian.githubuserapp2.R
import com.alfian.githubuserapp2.adapter.SectionPagerAdapter
import com.alfian.githubuserapp2.databinding.ActivityDetailBinding
import com.alfian.githubuserapp2.datasource.UserResponse
import com.alfian.githubuserapp2.networking.NetworkConnection
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detailDataLayout.visibility = View.GONE
        val user = intent.getParcelableExtra<UserResponse>(KEY_USER)
        if (user != null) {
            user.login?.let {
                checkInternetConnection(it)
            }
        }
    }

    private fun checkInternetConnection(username: String) {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, { isConnected ->
            if (isConnected) {
                showNoInternetAnimation(false)
                showFailedLoadData(false)
                val detailViewModel: DetailViewModel by viewModels {
                    DetailViewModelFactory(username)
                }
                detailViewModel.isLoading.observe(this, {
                    showProgressBar(it)
                })
                detailViewModel.isNoInternet.observe(this, {
                    showNoInternetAnimation(it)
                })
                detailViewModel.isDataFailed.observe(this, {
                    showNoInternetAnimation(it)
                })
                detailViewModel.detailUser.observe(this@DetailActivity, { userResponse ->
                    if (userResponse != null) {
                        setData(userResponse)
                        setTabLayoutAdapter(userResponse)
                    }
                })
            } else {
                binding.detailDataLayout.visibility = View.GONE
                binding.detailAnimationLayout.visibility = View.VISIBLE
                showFailedLoadData(true)
                showNoInternetAnimation(false)
            }
        })
    }

    private fun setTabLayoutAdapter(user: UserResponse) {
        val sectionPagerAdapter = SectionPagerAdapter(this@DetailActivity)
        sectionPagerAdapter.model = user
        binding.viewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }

    private fun setData(userResponse: UserResponse?) {
        if (userResponse != null) {
            with(binding) {
                detailDataLayout.visibility = View.VISIBLE
                detailImage.visibility = View.VISIBLE
                Glide.with(root)
                    .load(userResponse.avatarUrl)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    )
                    .circleCrop()
                    .into(binding.detailImage)
                detailName.visibility = View.VISIBLE
                detailUsername.visibility = View.VISIBLE
                detailName.text = userResponse.name
                detailUsername.text = userResponse.login
                if (userResponse.bio != null) {
                    detailBio.visibility = View.VISIBLE
                    detailBio.text = userResponse.bio
                } else {
                    detailBio.visibility = View.GONE
                }
                if (userResponse.company != null) {
                    detailCompany.visibility = View.VISIBLE
                    detailCompany.text = userResponse.company
                } else {
                    detailCompany.visibility = View.GONE
                }
                if (userResponse.location != null) {
                    detailLocation.visibility = View.VISIBLE
                    detailLocation.text = userResponse.location
                } else {
                    detailLocation.visibility = View.GONE
                }
                if (userResponse.blog != null) {
                    detailBlog.visibility = View.VISIBLE
                    detailBlog.text = userResponse.blog
                } else {
                    detailBlog.visibility = View.GONE
                }
                if (userResponse.followers != null) {
                    detailFollowersValue.visibility = View.VISIBLE
                    detailFollowersValue.text = userResponse.followers
                } else {
                    detailFollowersValue.visibility = View.GONE
                }
                if (userResponse.followers != null) {
                    detailFollowers.visibility = View.VISIBLE
                } else {
                    detailFollowers.visibility = View.GONE
                }
                if (userResponse.following != null) {
                    detailFollowingValue.visibility = View.VISIBLE
                    detailFollowingValue.text = userResponse.following
                } else {
                    detailFollowingValue.visibility = View.GONE
                }
                if (userResponse.following != null) {
                    detailFollowing.visibility = View.VISIBLE
                } else {
                    detailFollowing.visibility = View.GONE
                }
                if (userResponse.publicRepo != null) {
                    detailRepoValue.visibility = View.VISIBLE
                    detailRepoValue.text = userResponse.publicRepo
                } else {
                    detailRepoValue.visibility = View.GONE
                }
                if (userResponse.publicRepo != null) {
                    detailRepo.visibility = View.VISIBLE
                } else {
                    detailRepo.visibility = View.GONE
                }
            }
        } else {
            Log.i(TAG, "setData fun is error")
        }
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.detailLoader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showNoInternetAnimation(isNoInternet: Boolean) {
        binding.detailNoInternet.visibility = if (isNoInternet) View.VISIBLE else View.GONE
    }

    private fun showFailedLoadData(isFailed: Boolean) {
        binding.detailFailedDataLoad.visibility = if (isFailed) View.VISIBLE else View.GONE
    }

    companion object {
        const val KEY_USER = "user"
        private const val TAG = "DetailActivity"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}