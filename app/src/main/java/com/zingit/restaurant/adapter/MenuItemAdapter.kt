package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.databinding.SingleItemMenuOptionBinding

import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.models.item.ItemMenuState
import com.zingit.restaurant.models.item.VariationsModel
import com.zingit.restaurant.repository.FirebaseRepository
import com.zingit.restaurant.utils.Utils
import kotlin.coroutines.coroutineContext


class MenuItemAdapter(private val context: Context) : ListAdapter<ItemMenuModel, MenuItemAdapter.MenuViewHolder>(MenuDiffUtils()) {

    private  val TAG = "MenuItemAdapter"
    lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseRepository:FirebaseRepository
    private val viewPool = RecyclerView.RecycledViewPool()

    inner class MenuViewHolder(val binding: SingleItemMenuOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemModel: ItemMenuModel){
            binding.itemMenu = itemModel
            binding.activeornot = itemModel.active.equals("1")
            //Setting up variation rv

            val variationAdapter = VariationItemAdapter(context,itemModel.Id,itemModel.variations)
            binding.variationRv.layoutManager=LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
            variationAdapter.submitList(itemModel.variations)
            binding.variationRv.adapter=variationAdapter
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
        Log.d("MenuItemAdapter","itemModel:"+itemModel.toString())

        holder.bind(itemModel)
        holder.binding.apply {
            switchToggle.setOnCheckedChangeListener{ view, isChecked ->
                if(isChecked){
                    firestore.collection("test_menu").document(getItem(position).Id).update("active","1")
                }else{
                    firestore.collection("test_menu").document(getItem(position).Id).update("active","0")
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


