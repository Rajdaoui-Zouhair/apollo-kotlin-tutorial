package com.example.rocketreserver.ui.launch.list

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.rocketreserver.LaunchListQuery
import com.example.rocketreserver.core.Resource
import com.example.rocketreserver.core.http
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LaunchListViewModel @Inject constructor(
    private val apolloClient: ApolloClient
) : ViewModel() {
    private val _launchList =
        MutableStateFlow<Resource<List<LaunchListQuery.Launch>>>(Resource.Loading())
    val launchList: StateFlow<Resource<List<LaunchListQuery.Launch>>> = _launchList
    private var cursor: String? = null
    private var hasMore: Boolean = true

    val loadMore: () -> Unit

    init {
        fetchData()
        loadMore = { if (hasMore) fetchData() }
    }

    private fun fetchData() {
        http(
            call = apolloClient.query(LaunchListQuery(cursor = Optional.presentIfNotNull(cursor))),
            flow = _launchList,
            map = {
                cursor = it.launches.cursor
                hasMore = it.launches.hasMore
                it.launches.launches.filterNotNull()
            }
        )
    }
}

