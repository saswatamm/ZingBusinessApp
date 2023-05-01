package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.CheckboxSingleLayoutBinding
import com.zingit.restaurant.models.item.CancelItemModel
import kotlin.math.absoluteValue


class CancelSpecificItemsAdapter(val context: Context, val onClick: (CancelItemModel) -> Unit) :
    ListAdapter<CancelItemModel, CancelSpecificItemsAdapter.MyViewHolder>(
        CancelSpecificItemsDiffUtils()
    ) {
    private val TAG = "CancelSpecificItemsAdap"

    // List of items
    private var itemList: MutableList<CancelItemModel> = mutableListOf()


    // Boolean flag to indicate whether all items are selected or not
    private var isAllSelected = false

    // Set the list of items and notify data set changed


    // Return the list of selected items


    // Set all items as selected
    fun selectAll() {
        isAllSelected = true
        itemList.forEachIndexed { index, cancelItemModel ->

            if (index!=0){
                onClick(cancelItemModel)
            }
            cancelItemModel.isChecked = true

        }
        notifyDataSetChanged()

    }

    // Set all items as unselected
    fun unselectAll() {
        isAllSelected = false
        itemList.forEachIndexed { index, cancelItemModel ->

            if (index!=0){
                onClick(cancelItemModel)
            }
            cancelItemModel.isChecked = false
        }
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: CheckboxSingleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: CancelItemModel) {
            itemList.addAll(listOf(orderItem))
            binding.cancelOrder = orderItem

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        val binding =
            CheckboxSingleLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CancelSpecificItemsAdapter.MyViewHolder, position: Int) {
        val orderItem = getItem(position)
        holder.bind(orderItem)
        holder.binding.notAvailable.isChecked = orderItem.isChecked || isAllSelected

        // Set click listener for checkbox
        holder.binding.notAvailable.setOnClickListener {
            // Set checked state of item
            orderItem.isChecked = holder.binding.notAvailable.isChecked

            // Check/uncheck all items if the first item is checked/unchecked
            if (position == 0) {
                if (holder.binding.notAvailable.isChecked) {
                    selectAll()
                } else {
                    unselectAll()
                }
            }else{
                onClick(orderItem)
            }


        }


    }


    class CancelSpecificItemsDiffUtils : DiffUtil.ItemCallback<CancelItemModel>() {
        override fun areItemsTheSame(oldItem: CancelItemModel, newItem: CancelItemModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CancelItemModel,
            newItem: CancelItemModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}