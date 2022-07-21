package com.example.rocketreserver.ui.launch.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.rocketreserver.LaunchListQuery
import com.example.rocketreserver.R
import com.example.rocketreserver.core.ItemClick
import com.example.rocketreserver.databinding.LaunchItemBinding

class LaunchListAdapter(
    val itemClick: ItemClick<LaunchListQuery.Launch>
) : RecyclerView.Adapter<LaunchListAdapter.ViewHolder>() {

    private val items = mutableListOf<LaunchListQuery.Launch>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LaunchItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.launch_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addList(newItems: List<LaunchListQuery.Launch?>) {
        val endPosition = this.items.size - 1
        this.items.addAll(newItems.filterNotNull())
        notifyItemRangeInserted(endPosition, newItems.size)
    }

    inner class ViewHolder(private val binding: LaunchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(launch: LaunchListQuery.Launch) {
            binding.launch = launch
            binding.root.setOnClickListener { itemClick.onItemClick(launch) }
        }
    }
}


@BindingAdapter("url")
fun setUrl(imageView: ImageView, url: String?) {
    url?.let {
        imageView.load(url) {
            placeholder(R.drawable.ic_placeholder)
        }
    }
}