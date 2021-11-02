package com.alfian.githubuserapp2.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.alfian.githubuserapp2.R
import com.alfian.githubuserapp2.adapter.OnItemClickCallback
import com.alfian.githubuserapp2.adapter.UserAdapter
import com.alfian.githubuserapp2.databinding.ActivityMainBinding
import com.alfian.githubuserapp2.datasource.UserResponse
import com.alfian.githubuserapp2.networking.NetworkConnection
import com.alfian.githubuserapp2.ui.detail.DetailActivity

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private val adapter: UserAdapter by lazy {
        UserAdapter()
    }

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpSearchView()
        observeAnimationAndProgressBar()
        checkInternetConnection()
    }

    private fun setUpSearchView() {
        with(binding) {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    showFailedLoadData(false)
                    showProgressBar(true)
                    mainViewModel.getUserBySearch(query)
                    mainViewModel.searchUser.observe(this@MainActivity) { searchUserResponse ->
                        if (searchUserResponse != null) {
                            adapter.addDataToList(searchUserResponse)
                            setUserData()
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })

        }
    }

    private fun observeAnimationAndProgressBar() {
        mainViewModel.isLoading.observe(this, {
            showProgressBar(it)
        })
        mainViewModel.isDataFailed.observe(this, {
            showFailedLoadData(it)
        })
    }

    private fun checkInternetConnection() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, { isConnected ->
            if (isConnected) {
                showFailedLoadData(false)
                mainViewModel.user.observe(this, { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                })
                mainViewModel.searchUser.observe(this@MainActivity) { searchUserResponse ->
                    if (searchUserResponse != null) {
                        adapter.addDataToList(searchUserResponse)
                        binding.rvMain.visibility = View.VISIBLE
                    }
                }
            } else {
                mainViewModel.user.observe(this, { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                })
                mainViewModel.searchUser.observe(this@MainActivity) { searchUserResponse ->
                    if (searchUserResponse != null) {
                        adapter.addDataToList(searchUserResponse)
                        binding.rvMain.visibility = View.VISIBLE
                    }
                }
                Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun hideUserList() {
        binding.rvMain.layoutManager = null
        binding.rvMain.adapter = null
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.animLoader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @Suppress("SameParameterValue")
    private fun showFailedLoadData(isFailed: Boolean) {
        binding.animFailedDataLoad.visibility = if (isFailed) View.VISIBLE else View.GONE
        binding.tvFailed.visibility = if (isFailed) View.VISIBLE else View.GONE
    }

    private fun setUserData() {
        val layoutManager =
            GridLayoutManager(this@MainActivity, 2, GridLayoutManager.HORIZONTAL, false)
        binding.rvMain.layoutManager = layoutManager
        binding.rvMain.setHasFixedSize(true)
        binding.rvMain.adapter = adapter
        adapter.setOnItemClickCallback(object : OnItemClickCallback {
            override fun onItemClicked(user: UserResponse) {
                hideUserList()
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.KEY_USER, user)
                startActivity(intent)
            }
        })
    }
}
