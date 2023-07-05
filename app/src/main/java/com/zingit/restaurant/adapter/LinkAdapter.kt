package com.zingit.restaurant.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.VolumeItemBinding
import com.zingit.restaurant.models.order.OrdersModel

class LinkAdapter(val context: Context):
    androidx.recyclerview.widget.ListAdapter<OrdersModel, LinkAdapter.MyViewHolder>(LinkDiffUtils())  {



    inner class MyViewHolder(val binding:VolumeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ordersModel:  OrdersModel) {

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkAdapter.MyViewHolder {
        val binding =
            VolumeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val orderQR =getItem(position)
        holder.bind(orderQR)
    }

}

class LinkDiffUtils : DiffUtil.ItemCallback<OrdersModel>() {
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