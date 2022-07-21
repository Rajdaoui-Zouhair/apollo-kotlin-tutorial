package com.example.rocketreserver.ui.launch.details

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.example.rocketreserver.BookTripsMutation
import com.example.rocketreserver.CancelTripMutation
import com.example.rocketreserver.LaunchQuery
import com.example.rocketreserver.core.Resource
import com.example.rocketreserver.core.http
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LaunchDetailsViewModel @Inject constructor(
    private val apolloClient: ApolloClient
) : ViewModel() {

    private val _launchDetails: MutableStateFlow<Resource<LaunchQuery.Launch>> =
        MutableStateFlow(Resource.Loading())
    val launchDetails: StateFlow<Resource<LaunchQuery.Launch>> = _launchDetails

    private val _bookCancelTrip: MutableStateFlow<Resource<Any>> =
        MutableStateFlow(Resource.Loading())
    val bookCancelTrip: StateFlow<Resource<Any>> = _bookCancelTrip

    fun bookTrip(id: String, booked: Boolean?) {
        http(
            call = if (booked!!) apolloClient.mutation(CancelTripMutation(id))
            else apolloClient.mutation(
                BookTripsMutation(listOf(id))
            ),
            flow = _bookCancelTrip,
            map = {
                when (it) {
                    is BookTripsMutation.Data -> it.bookTrips
                    is CancelTripMutation.Data -> it.cancelTrip
                    else -> {}
                }
            }
        )
    }

    fun fetchLaunchDetails(id: String) {
        http(
            call = apolloClient.query(LaunchQuery(id = id)),
            flow = _launchDetails,
            map = { it.launch }
        )
    }
}