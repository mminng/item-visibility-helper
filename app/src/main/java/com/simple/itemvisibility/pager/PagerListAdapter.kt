package com.simple.itemvisibility.pager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.itemvisibility.databinding.ItemPagerListBinding

/**
 * Created by zh on 2023/4/27.
 */
class PagerListAdapter constructor(private val data: List<String>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPagerListBinding =
            ItemPagerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        bindItemClick(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = data[position]
        holder.bind(position)
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

class ViewHolder constructor(private val binding: ItemPagerListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int) {
        binding.itemPTopPosition.text = "$position"
        binding.itemPBottomPosition.text = "$position"
    }
}