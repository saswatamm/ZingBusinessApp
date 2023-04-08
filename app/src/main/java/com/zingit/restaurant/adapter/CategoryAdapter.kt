package com.zingit.restaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zingit.restaurant.databinding.ItemViewBinding
import com.zingit.restaurant.databinding.MenuCategoryLayoutBinding
import com.zingit.restaurant.models.OrderItem
import com.zingit.restaurant.models.item.CategoryModel

class CategoryAdapter(private val context: Context)  : ListAdapter<CategoryModel,CategoryAdapter.CategoryViewHolder>(CategoryDiffUtils()) {

    inner class CategoryViewHolder(val binding: MenuCategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryModel: CategoryModel) {
            binding.category = categoryModel
            Glide.with(context).load(categoryModel.itemImage).into(binding.categoryImage)


        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        val binding = MenuCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryViewHolder, position: Int) {
        val categoryModel =getItem(position)
        holder.bind(categoryModel)
    }
}


class CategoryDiffUtils: DiffUtil.ItemCallback<CategoryModel>() {
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