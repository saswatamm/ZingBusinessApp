package com.zingit.restaurant.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.SingleItemHistoryBinding
import com.zingit.restaurant.models.order.OrdersModel
import java.text.DateFormat
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HistoryAdapter.MyViewHolder, position: Int) {
        val orderHistory = getItem(position)
        holder.bind(orderHistory)

//Check whats happening here once
        val timeString=orderHistory.order?.details!!.createdOn.substringAfter(" ")
        val dateString=orderHistory.order?.details!!.createdOn.substringBefore(" ")
        val dateTime=dateString+"T"+timeString
        val outputFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        val outputText = outputFormat.format(date)

        holder.binding.tvTime.text = outputText
        holder.binding.view.setOnClickListener{
            onClick(orderHistory)
        }
//        val date = Date(orderHistory.placedTime.seconds * 1000)
//        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//        val formattedTime = dateFormat.format(date)
//        holder.binding.tvTime.text = formattedTime
//        holder.binding.view.setOnClickListener{
//            onClick(orderHistory)
//        }

    }
}

class HistoryOrderDiffUtils : DiffUtil.ItemCallback<OrdersModel>() {
    override fun areItemsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
        return oldItem.order?.details?.orderId == newItem.order?.details?.orderId
        return true
    }

    override fun areContentsTheSame(
        oldItem: OrdersModel,
        newItem: OrdersModel
    ): Boolean {
        return oldItem == newItem
    }


}