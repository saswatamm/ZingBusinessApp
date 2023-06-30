package com.zingit.restaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.ItemSingleOrderLayoutBinding
import com.zingit.restaurant.databinding.SingleItemHistoryBinding
import com.zingit.restaurant.models.order.OrderItem
import com.zingit.restaurant.models.order.OrderItemDetails
import com.zingit.restaurant.models.order.OrdersModel

class PastOrderAdapter(val context: Context) : ListAdapter<OrderItemDetails, PastOrderAdapter.MyViewHolder>(PastOrderDiffUtils()) {

    inner class MyViewHolder(val binding: ItemSingleOrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: OrderItemDetails) {
            binding.order = orderItem


        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PastOrderAdapter.MyViewHolder {
        val binding = ItemSingleOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PastOrderAdapter.MyViewHolder, position: Int) {
        val orderItem = getItem(position)
        holder.bind(orderItem)

    }

}

class PastOrderDiffUtils : DiffUtil.ItemCallback<OrderItemDetails>() {
    override fun areItemsTheSame(
        oldItem: OrderItemDetails,
        newItem: OrderItemDetails
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: OrderItemDetails,
        newItem: OrderItemDetails
    ): Boolean {
        return oldItem == newItem
    }


}