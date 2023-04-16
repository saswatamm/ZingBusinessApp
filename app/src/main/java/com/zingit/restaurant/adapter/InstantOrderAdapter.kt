package com.zingit.restaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.InstantOrdersBinding
import com.zingit.restaurant.databinding.ItemViewBinding
import com.zingit.restaurant.models.order.OrdersModel

class InstantOrderAdapter(val context : Context):ListAdapter<OrdersModel,InstantOrderAdapter.MyViewHolder>(InstantOrderDiffUtils()) {
    inner class MyViewHolder(val binding:InstantOrdersBinding ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ordersModel:  OrdersModel) {
            binding.orderModel = ordersModel
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InstantOrderAdapter.MyViewHolder {
        val binding =
            InstantOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstantOrderAdapter.MyViewHolder, position: Int) {
        val orderHistory =getItem(position)
        holder.bind(orderHistory)
    }
}

class InstantOrderDiffUtils : DiffUtil.ItemCallback<OrdersModel>() {
    override fun areItemsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
        return oldItem.paymentOrderID == newItem.paymentOrderID
    }

    override fun areContentsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
        return oldItem == newItem
    }


}