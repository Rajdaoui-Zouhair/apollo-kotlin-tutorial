package com.example.rocketreserver.ui.launch.list

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.rocketreserver.LaunchListQuery
import com.example.rocketreserver.R
import com.example.rocketreserver.core.ItemClick
import com.example.rocketreserver.core.Resource
import com.example.rocketreserver.databinding.LaunchListFragmentBinding
import com.example.rocketreserver.ui.launch.LaunchBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class LaunchListFragment :
    LaunchBaseFragment<LaunchListFragmentBinding, LaunchListViewModel, List<LaunchListQuery.Launch>>(
        R.layout.launch_list_fragment
    ), ItemClick<LaunchListQuery.Launch> {

    private val launchListAdapter = LaunchListAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.bindRecyclerView()
    }

    private fun LaunchListFragmentBinding.bindRecyclerView() {
        launches.adapter = launchListAdapter
        launches.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
        launches.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        viewModel.loadMore()
                    }
                }
            }
        )
    }

    override fun onSuccess(resource: List<LaunchListQuery.Launch>) {
        handleLoadingVisibility(false)
        handleErrorVisibility(false)
        launchListAdapter.addList(resource)
    }

    override fun onError(error: Exception) {
        handleLoadingVisibility(false)
        handleErrorVisibility(true)
    }

    override fun handleErrorVisibility(isLoading: Boolean) {
    }

    override fun handleLoadingVisibility(isLoading: Boolean) {
        binding.isLoading = isLoading
    }

    override fun getViewModelClass(): Class<LaunchListViewModel> {
        return LaunchListViewModel::class.java
    }

    override fun getResource(): StateFlow<Resource<List<LaunchListQuery.Launch>>> {
        return viewModel.launchList
    }

    override fun onItemClick(item: LaunchListQuery.Launch) {
        findNavController().navigate(LaunchListFragmentDirections.openLaunchDetails(item.id))
    }
}
