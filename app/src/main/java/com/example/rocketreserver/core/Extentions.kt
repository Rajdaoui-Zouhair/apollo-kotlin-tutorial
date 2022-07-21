package com.example.rocketreserver.core

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun <D : Operation.Data, R> ViewModel.http(
    call: ApolloCall<D>,
    flow: MutableStateFlow<Resource<R>>,
    map: (D) -> R?
) {
    viewModelScope.launch {
        val response = try {
            call.execute()
        } catch (e: ApolloException) {
            flow.emit(Resource.Error(error = e))
            return@launch
        }
        if (response.hasErrors()) {
            flow.emit(Resource.Error(error = Exception()))
            return@launch
        }
        response.data?.let { data ->
            map(data)?.let {
                flow.emit(Resource.Success(it))
            }
        }
    }
}

@BindingAdapter("showHide")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}