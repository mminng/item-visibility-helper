package com.simple.itemvisibility.horizontallist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.itemvisibility.databinding.ItemHorizontalListBinding

/**
 * Created by zh on 2023/4/27.
 */
class HorizontalListAdapter constructor(private val data: List<String>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemHorizontalListBinding =
            ItemHorizontalListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        bindItemClick(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = data[position]
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    fun setOnItemClickListener(listener: (item: String, position: Int) -> Unit) {
        _itemClick = listener
    }

    private var _itemClick: ((item: String, position: Int) -> Unit)? = null

    private fun bindItemClick(holder: ViewHolder) {
        holder.itemView.setOnClickListener {
            _itemClick?.let {
                val position = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                val data: String = holder.itemView.tag as String
                it.invoke(data, position)
            }
        }
    }
}

class ViewHolder constructor(private val binding: ItemHorizontalListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(url: String, position: Int) {
        binding.itemHTopPosition.text = "$position"
        binding.itemHBottomPosition.text = "$position"
    }
}