package com.zingit.restaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.VolumeItemBinding
import com.zingit.restaurant.models.VolumeModel
import com.zingit.restaurant.models.order.OrdersModel

class QrVolumeAdapter(val context: Context, val onClick: (OrdersModel) -> Unit):
    ListAdapter<OrdersModel, QrVolumeAdapter.MyViewHolder>(QrDiffUtils())  {



    inner class MyViewHolder(val binding:VolumeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ordersModel:  OrdersModel) {
            binding.orderModel = ordersModel

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QrVolumeAdapter.MyViewHolder {
        val binding =
            VolumeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val orderQR =getItem(position)
        holder.bind(orderQR)
        holder.binding.viewButton.setOnClickListener {
            onClick(orderQR) }
    }

}

class QrDiffUtils : DiffUtil.ItemCallback<OrdersModel>() {
    override fun areItemsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
        return oldItem.order!!.details!!.orderId == newItem.order!!.details!!.orderId
    }

    override fun areContentsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
        return oldItem == newItem
    }



}