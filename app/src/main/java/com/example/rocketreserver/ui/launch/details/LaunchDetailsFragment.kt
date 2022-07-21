package com.example.rocketreserver.ui.launch.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rocketreserver.BookTripsMutation
import com.example.rocketreserver.CancelTripMutation
import com.example.rocketreserver.LaunchQuery
import com.example.rocketreserver.R
import com.example.rocketreserver.core.Resource
import com.example.rocketreserver.core.User
import com.example.rocketreserver.databinding.LaunchDetailsFragmentBinding
import com.example.rocketreserver.ui.launch.LaunchBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LaunchDetailsFragment :
    LaunchBaseFragment<LaunchDetailsFragmentBinding, LaunchDetailsViewModel, LaunchQuery.Launch>(
        R.layout.launch_details_fragment
    ) {

    private val args: LaunchDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchLaunchDetails(args.launchId)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        lifecycleScope.launchWhenStarted {
            viewModel.bookCancelTrip.collectLatest {
                when (it) {
                    is Resource.Loading -> handleLoadingVisibility(true)
                    is Resource.Success -> onBookCancelSuccess(it.data)
                    is Resource.Error -> onError(it.error)
                }
            }
        }

        binding.bookButton.setOnClickListener { bookTrip() }
    }

    private fun onBookCancelSuccess(data: Any) {
        when (data) {
            is BookTripsMutation.BookTrips -> binding.booked = true
            is CancelTripMutation.CancelTrip -> binding.booked = false
        }
    }

    private fun bookTrip() {
        User.getToken(requireContext())?.let {
            viewModel.bookTrip(args.launchId, binding.booked)
        } ?: kotlin.run {
            findNavController().navigate(LaunchDetailsFragmentDirections.openLogin())
        }
    }

    override fun onSuccess(resource: LaunchQuery.Launch) {
        handleLoadingVisibility(false)
        handleErrorVisibility(false)
        binding.launch = resource
        binding.booked = resource.isBooked
    }

    override fun onError(error: Exception) {
        handleLoadingVisibility(false)
        handleErrorVisibility(true)
    }

    override fun handleLoadingVisibility(isLoading: Boolean) {
        binding.isLoading = isLoading
    }

    override fun handleErrorVisibility(isLoading: Boolean) {
        binding.error.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun getViewModelClass(): Class<LaunchDetailsViewModel> {
        return LaunchDetailsViewModel::class.java
    }

    override fun getResource(): StateFlow<Resource<LaunchQuery.Launch>> {
        return viewModel.launchDetails
    }
}
