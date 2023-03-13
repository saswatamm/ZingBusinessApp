package com.zingit.restaurant.adapter

import com.zingit.restaurant.databinding.ItemViewBinding
import com.zingit.restaurant.models.OrderItem
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView



class ItemAdapter(val context: Context) : ListAdapter<OrderItem,ItemAdapter.MyViewHolder>(MyDiffUtilsAds()) {
    inner class MyViewHolder(val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(paymentModel: OrderItem) {
            binding.paymentModel = paymentModel

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val orderHistory =getItem(position)
        holder.bind(orderHistory)


    }

}

class MyDiffUtilsAds : DiffUtil.ItemCallback<OrderItem>() {
    override fun areItemsTheSame(
        oldItem: OrderItem,
        newItem: OrderItem
    ): Boolean {
        return oldItem.itemID == newItem.itemID
    }

    override fun areContentsTheSame(
        oldItem: OrderItem,
        newItem: OrderItem
    ): Boolean {
        return oldItem == newItem
    }

}
