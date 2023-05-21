package com.simple.itemvisibility.gird

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.simple.itemvisibility.databinding.ItemGridListBinding
import com.simple.itemvisibility.databinding.ItemGridVideoBinding

/**
 * Created by zh on 2023/4/27.
 */
class GridListAdapter constructor(private val data: List<ListModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ViewBinding
        val holder: RecyclerView.ViewHolder
        if (viewType == 0) {
            binding =
                ItemGridVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            holder = VideoViewHolder(binding)
        } else {
            binding =
                ItemGridListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            holder = ListViewHolder(binding)
        }
        bindItemClick(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tag = data[position]
        if (holder.itemViewType == 0) {
            (holder as VideoViewHolder).bind(position)
        } else {
            (holder as ListViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isVideoType) {
            0
        } else {
            1
        }
    }

    fun setOnItemClickListener(listener: (item: ListModel, position: Int) -> Unit) {
        _itemClick = listener
    }

    private var _itemClick: ((item: ListModel, position: Int) -> Unit)? = null

    private fun bindItemClick(holder: RecyclerView.ViewHolder) {
        holder.itemView.setOnClickListener {
            _itemClick?.let {
                val position = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                val data: ListModel = holder.itemView.tag as ListModel
                it.invoke(data, position)
            }
        }
    }
}

class VideoViewHolder constructor(private val binding: ItemGridVideoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int) {
        binding.itemGTopPosition.text = "$position"
        binding.itemGBottomPosition.text = "$position"
    }
}

class ListViewHolder constructor(private val binding: ItemGridListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int) {
        binding.itemGPosition.text = "$position"
    }
}