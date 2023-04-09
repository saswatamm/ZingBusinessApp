package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.ItemViewBinding
import com.zingit.restaurant.databinding.MenuCategoryLayoutBinding
import com.zingit.restaurant.databinding.SingleItemHistoryBinding
import com.zingit.restaurant.databinding.SingleItemMenuOptionBinding
import com.zingit.restaurant.models.OrderItem

import com.zingit.restaurant.models.item.ItemMenuModel


class MenuItemAdapter(private val context: Context) : ListAdapter<ItemMenuModel, MenuItemAdapter.MenuViewHolder>(MenuDiffUtils()) {

    private  val TAG = "MenuItemAdapter"

    inner class MenuViewHolder(val binding: SingleItemMenuOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemModel: ItemMenuModel){

            binding.itemMenu = itemModel


        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuItemAdapter.MenuViewHolder {
        val binding = SingleItemMenuOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemAdapter.MenuViewHolder, position: Int) {
        val itemModel =getItem(position)
        holder.bind(itemModel)
    }

}

class MenuDiffUtils : DiffUtil.ItemCallback<ItemMenuModel>() {
    override fun areItemsTheSame(
        oldItem: ItemMenuModel,
        newItem: ItemMenuModel
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: ItemMenuModel,
        newItem: ItemMenuModel
    ): Boolean {
        return oldItem == newItem
    }

}


