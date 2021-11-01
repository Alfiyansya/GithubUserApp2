package com.alfian.githubuserapp2.ui.follows

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfian.githubuserapp2.adapter.OnItemClickCallback
import com.alfian.githubuserapp2.adapter.UserFollowsAdapter
import com.alfian.githubuserapp2.databinding.FragmentFollowsBinding
import com.alfian.githubuserapp2.datasource.UserResponse
import com.alfian.githubuserapp2.ui.detail.DetailActivity

class FollowsFragment : Fragment() {
    private var _binding: FragmentFollowsBinding? = null
    private val binding get() = _binding!!
    private val adapter: UserFollowsAdapter by lazy {
        UserFollowsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)
        val user = arguments?.getParcelable<UserResponse>(ARG_PARCEL)

        if (index == 1) {
            user?.login?.let {
                val mIndex = 1
                setViewModel(it, mIndex)
            }
        } else {
            user?.login?.let {
                val mIndex = 2
                setViewModel(it, mIndex)
            }
        }
    }

    private fun setViewModel(username: String, index: Int) {
        val followsViewModel: FollowsViewModel by viewModels {
            FollowsViewModelFactory(username)
        }
        followsViewModel.isLoading.observe(viewLifecycleOwner, {
            showProgressBar(it)
        })
        followsViewModel.isDataFailed.observe(viewLifecycleOwner, {
            showFailedLoadData(it)
        })
        if (index == 1) {
            followsViewModel.followers.observe(viewLifecycleOwner, { follResponse ->
                if (follResponse != null) {
                    setUserData(follResponse)
                }
            })
        } else {
            followsViewModel.following.observe(viewLifecycleOwner, { follResponse ->
                if (follResponse != null) {
                    setUserData(follResponse)
                }
            })

        }
    }

    private fun setUserData(userResponse: ArrayList<UserResponse>) {
        if (userResponse.isNotEmpty()) {
            adapter.addDataToList(userResponse)
            with(binding) {
                val layoutManager = LinearLayoutManager(view?.context)
                rvFollows.layoutManager = layoutManager
                rvFollows.adapter = adapter
                adapter.setOnItemClickCallback(object : OnItemClickCallback {
                    override fun onItemClicked(user: UserResponse) {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra(DetailActivity.KEY_USER, user)
                        startActivity(intent)
                    }
                })
            }
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state)
            binding.animLoader.visibility = View.VISIBLE
        else
            binding.animLoader.visibility = View.GONE
    }

    private fun showFailedLoadData(isFailed: Boolean) {
        binding.animFailedDataLoad.visibility = if (isFailed) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_PARCEL = "user_model"

        @JvmStatic
        fun newInstance(index: Int, userResponse: UserResponse?) =
            FollowsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, index)
                    putParcelable(ARG_PARCEL, userResponse)
                }
            }
    }

}