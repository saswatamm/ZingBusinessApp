package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.CheckboxLayoutSingleBinding
import com.zingit.restaurant.models.order.OrdersModel

class CancelItemAdapter(val context: Context,val onClick: (String) -> Unit) :
    ListAdapter<String, CancelItemAdapter.MyViewHolder>(CancelOrderItemDiffUtils()) {
    private  val TAG = "CancelItemAdapter"

    inner class MyViewHolder(val binding: CheckboxLayoutSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: String) {

            binding.cancelOrder = orderItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CheckboxLayoutSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val orderItem = getItem(position)
        holder.bind(orderItem)
        Log.e(TAG, "onBindViewHolder: $orderItem", )

    }

}

class CancelOrderItemDiffUtils : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem == newItem
    }


}
