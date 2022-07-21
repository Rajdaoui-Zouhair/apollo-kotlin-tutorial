package com.example.rocketreserver.core

import androidx.recyclerview.widget.DiffUtil
import com.example.rocketreserver.LaunchListQuery

object Constants {
    const val BASE_URL = "https://apollo-fullstack-tutorial.herokuapp.com/graphql"
}

object LaunchDiffCallback : DiffUtil.ItemCallback<LaunchListQuery.Launch>() {
    override fun areItemsTheSame(
        oldItem: LaunchListQuery.Launch,
        newItem: LaunchListQuery.Launch
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LaunchListQuery.Launch,
        newItem: LaunchListQuery.Launch
    ): Boolean {
        return oldItem == newItem
    }
}