package com.simple.itemvisibility.flex

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.itemvisibility.databinding.ItemFlexListBinding

/**
 * Created by zh on 2023/4/27.
 */
class FlexListAdapter constructor(private val data: List<ListModel>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFlexListBinding =
            ItemFlexListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        bindItemClick(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = data[position]
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    fun setOnItemClickListener(listener: (item: ListModel, position: Int) -> Unit) {
        _itemClick = listener
    }

    private var _itemClick: ((item: ListModel, position: Int) -> Unit)? = null

    private fun bindItemClick(holder: ViewHolder) {
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

class ViewHolder constructor(private val binding: ItemFlexListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ListModel, position: Int) {
        binding.itemFTopPosition.text = "$position"
        binding.itemFBottomPosition.text = "$position"
        binding.itemFTitle.text = item.title
    }
}