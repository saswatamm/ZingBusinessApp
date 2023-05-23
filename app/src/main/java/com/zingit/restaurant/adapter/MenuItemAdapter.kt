package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.databinding.SingleItemMenuOptionBinding

import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.utils.Utils


class MenuItemAdapter(private val context: Context) : ListAdapter<ItemMenuModel, MenuItemAdapter.MenuViewHolder>(MenuDiffUtils()) {

    private  val TAG = "MenuItemAdapter"
    lateinit var firestore: FirebaseFirestore

    inner class MenuViewHolder(val binding: SingleItemMenuOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemModel: ItemMenuModel){

            binding.itemMenu = itemModel

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuItemAdapter.MenuViewHolder {
        val binding = SingleItemMenuOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemAdapter.MenuViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        val itemModel =getItem(position)
        holder.bind(itemModel)
        holder.binding.apply {
            switchToggle.setOnCheckedChangeListener{ view, isChecked ->
                if(isChecked){
                    firestore.collection("test_menu").document(getItem(position).itemId).update("active","1")
                }else{
                    firestore.collection("test_menu").document(getItem(position).itemId).update("active","0")
                }
            }
        }
    }

}

class MenuDiffUtils : DiffUtil.ItemCallback<ItemMenuModel>() {
    override fun areItemsTheSame(
        oldItem: ItemMenuModel,
        newItem: ItemMenuModel
    ): Boolean {
        return oldItem.itemName == newItem.itemName
    }

    override fun areContentsTheSame(
        oldItem: ItemMenuModel,
        newItem: ItemMenuModel
    ): Boolean {
        return oldItem == newItem
    }

}


