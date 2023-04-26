package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.CheckboxLayoutSingleBinding
import com.zingit.restaurant.databinding.ItemSingleOrderLayoutBinding
import com.zingit.restaurant.models.order.OrdersModel
import okhttp3.internal.notify
import kotlin.math.log

class CancelSpecificItemsAdapter(val context: Context, val onClick: (String) -> Unit) :
    ListAdapter<String, CancelSpecificItemsAdapter.MyViewHolder>(CancelSpecificItemsDiffUtils()) {
    private val TAG = "CancelSpecificItemsAdap"

    inner class MyViewHolder(val binding: CheckboxLayoutSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: String) {
            binding.cancelOrder = orderItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val binding =
            CheckboxLayoutSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CancelSpecificItemsAdapter.MyViewHolder, position: Int) {
        val orderItem = getItem(position)
        holder.bind(orderItem)
        holder.binding.notAvailable.setOnClickListener {
            onClick(orderItem)
            Log.e(TAG, "onBindViewHolder: clickeddd")
            Log.e(TAG, "onBindViewHolder: ${holder.binding.notAvailable.isChecked}", )
        }


    }
}


class CancelSpecificItemsDiffUtils : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}