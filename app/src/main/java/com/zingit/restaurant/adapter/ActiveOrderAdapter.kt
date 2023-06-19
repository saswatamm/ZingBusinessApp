package com.zingit.restaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.ItemViewBinding
import com.zingit.restaurant.databinding.SingleItemTicketBinding
import com.zingit.restaurant.models.item.CategoryModel
import com.zingit.restaurant.models.order.OrderItem
import com.zingit.restaurant.models.order.OrdersModel

class ActiveOrderAdapter(val context: Context, val onClick: (OrdersModel) -> Unit) :ListAdapter<OrdersModel,ActiveOrderAdapter.MyViewHolder>(ActiveOrderDiffUtils()) {

    inner class MyViewHolder(val binding: SingleItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ordersModel:  OrdersModel) {
           binding.orderModel = ordersModel

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActiveOrderAdapter.MyViewHolder {
        val binding =
            SingleItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActiveOrderAdapter.MyViewHolder, position: Int) {
        val orderHistory =getItem(position)
        holder.bind(orderHistory)
        holder.itemView.setOnClickListener {
            onClick(orderHistory) }
    }
}

class ActiveOrderDiffUtils : DiffUtil.ItemCallback<OrdersModel>() {
    override fun areItemsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
//        return oldItem.order!!.orderId == newItem.order!!.orderId
        return true
    }

    override fun areContentsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
        return oldItem == newItem
    }



}
