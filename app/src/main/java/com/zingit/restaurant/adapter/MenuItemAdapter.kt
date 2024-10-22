package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.databinding.SingleItemMenuOptionBinding
import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.repository.FirebaseRepository
import com.zingit.restaurant.viewModel.ExploreViewModel


class MenuItemAdapter(private val context: Context) : ListAdapter<ItemMenuModel, MenuItemAdapter.MenuViewHolder>(MenuDiffUtils()) {

    private  val TAG = "MenuItemAdapter"
    lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseRepository:FirebaseRepository
    private val viewPool = RecyclerView.RecycledViewPool()


    inner class MenuViewHolder(val binding: SingleItemMenuOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemModel: ItemMenuModel){
            binding.itemMenu = itemModel
            var isVariations:Boolean=false
            var isZero:Boolean=false
            if (itemModel.variations.size==0)
                isZero=true
            else
            {
                isZero=false
                itemModel.variations.forEach {
                    isVariations=isVariations||it.active=="1"
                }
                if(!isVariations)
                    itemModel.active="0"
            }

            binding.activeornot = itemModel.active=="1"
                    //&& (isVariations || isZero)
            //Setting up variation rv
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
        var itemModel =getItem(position)
        val variationAdapter = VariationItemAdapter(context,itemModel.Id,itemModel.variations)

        holder.binding.apply {

            switchToggle.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    firestore.collection("prod_menu").document(getItem(position).Id)
                        .update("active", "1")
                    itemModel.active="1"

                } else {
                    firestore.collection("prod_menu").document(getItem(position).Id)
                        .update("active", "0")
                        itemModel.active="0"
//                    itemModel.variations.forEach {
//                        it.active = "0"
//                    }
//                    firestore.collection("prod_menu").document(getItem(position).Id)
//                        .update("variations", itemModel.variations)

//                    holder.binding.variationRv.post(Runnable { variationAdapter.notifyDataSetChanged() })
                }
            }

            variationRv.apply {
                layoutManager = LinearLayoutManager(
                    holder.binding.root.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = variationAdapter
                setRecycledViewPool(viewPool)
            }
        }
        variationAdapter.submitList(itemModel.variations)
        holder.binding.executePendingBindings()
        holder.bind(itemModel)
    }

}

class MenuDiffUtils : DiffUtil.ItemCallback<ItemMenuModel>() {
    override fun areItemsTheSame(
        oldItem: ItemMenuModel,
        newItem: ItemMenuModel
    ): Boolean {
//        var i=oldItem.variations.size-1
//        while(i>=0)
//        {
//            if(oldItem.variations[i]!=newItem.variations[i])
//            {
//                return false
//            }
//            i--
//        }
        return (oldItem.itemName == newItem.itemName && oldItem.active ==newItem.active)
    }

    override fun areContentsTheSame(
        oldItem: ItemMenuModel,
        newItem: ItemMenuModel
    ): Boolean {

        return oldItem == newItem
    }

}


