package com.zingit.restaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.SingleItemHistoryBinding
import com.zingit.restaurant.databinding.SingleItemTicketBinding
import com.zingit.restaurant.models.order.OrdersModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(val context: Context, val onClick: (OrdersModel) -> Unit) :
    ListAdapter<OrdersModel, HistoryAdapter.MyViewHolder>(HistoryOrderDiffUtils()) {

    inner class MyViewHolder(val binding: SingleItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ordersModel: OrdersModel) {
            binding.history = ordersModel

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.MyViewHolder {
        val binding =
            SingleItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.MyViewHolder, position: Int) {
        val orderHistory = getItem(position)
        holder.bind(orderHistory)
        val date = Date(orderHistory.placedTime.seconds * 1000)
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(date)
        holder.binding.tvTime.text = formattedTime
        holder.binding.view.setOnClickListener{
            onClick(orderHistory)
        }

    }
}

class HistoryOrderDiffUtils : DiffUtil.ItemCallback<OrdersModel>() {
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