package com.zingit.restaurant.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zingit.restaurant.databinding.MenuCategoryLayoutBinding
import com.zingit.restaurant.models.item.CategoryModel

class CategoryAdapter(
    private val context: Context,
    private var p1: Int,
    val onClick: (CategoryModel) -> Unit
) : ListAdapter<CategoryModel, CategoryAdapter.CategoryViewHolder>(CategoryDiffUtils()) {

    private var selectedPosition: Int = -1

    inner class CategoryViewHolder(val binding: MenuCategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryModel: CategoryModel, position: Int) {
            binding.category = categoryModel
            Glide.with(context).load(categoryModel.itemImage).into(binding.profileImage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        val binding =
            MenuCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryAdapter.CategoryViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val categoryModel = getItem(position)
        holder.bind(categoryModel, position)

        holder.binding.root.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onClick(categoryModel)
        }
        if (selectedPosition == position || p1==0) {
            p1=-1
            holder.binding.selector.visibility = View.VISIBLE
        } else {
            holder.binding.selector.visibility = View.GONE

        }

    }
}

class CategoryDiffUtils : DiffUtil.ItemCallback<CategoryModel>() {
    override fun areItemsTheSame(
        oldItem: CategoryModel,
        newItem: CategoryModel
    ): Boolean {
        return oldItem.category == newItem.category
    }

    override fun areContentsTheSame(
        oldItem: CategoryModel,
        newItem: CategoryModel
    ): Boolean {
        return oldItem == newItem
    }

}