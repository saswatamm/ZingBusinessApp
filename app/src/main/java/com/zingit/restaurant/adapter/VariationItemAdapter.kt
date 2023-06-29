package com.zingit.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.databinding.VariationItemMenuOptionBinding
import com.zingit.restaurant.models.item.VariationsModel


//send menuId from adapter
class VariationItemAdapter(private val context: Context,private val menuItemId :String,private val variationArray : ArrayList<VariationsModel>):
    ListAdapter<VariationsModel, VariationItemAdapter.VariationViewHolder>(VariationDiffUtils()) {   //send variationsmodel from MenuFragment

    //variationArray passed as args has all the array elements under variation
    private val TAG = "VariationItemAdapter"
    lateinit var firestore: FirebaseFirestore

    inner class VariationViewHolder(val binding: VariationItemMenuOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(variationsModel: VariationsModel) {
            binding.variaitonMenu = variationsModel
            binding.activeornot = variationsModel.active.equals("1")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VariationItemAdapter.VariationViewHolder {
        val binding = VariationItemMenuOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VariationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VariationItemAdapter.VariationViewHolder, position: Int) {
        firestore = FirebaseFirestore.getInstance()
        val variationsModel = getItem(position)
        Log.d("MenuItemAdapter", "itemModel:" + variationsModel.toString())

        holder.bind(variationsModel)
        holder.binding.apply {
            variationSwitchToggle.setOnCheckedChangeListener { view, isChecked ->    //CHANGE THIS
                if (isChecked) {
                    variationArray.forEach {
                        if (it.variationId == variationsModel.variationId) {
                            it.active="1"
                            firestore.collection("prod_menu").document(menuItemId)
                                .update("variations", variationArray)
                            Log.d(TAG,variationArray.toString())
                            variationsModel.active="1"
                        }
                    }

                } else {
                    variationArray.forEach {
                        if (it.variationId == variationsModel.variationId) {
                            it.active="0"
                            firestore.collection("prod_menu").document(menuItemId)
                                .update("variations", variationArray)
                            Log.d(TAG,variationArray.toString())
                            variationsModel.active="0"
                        }
                    }

//                    if(variationArray.all { it.active=="0" })
//                    {
//                        firestore.collection("prod_menu").document(menuItemId)
//                            .update("active", "0")
//                        Log.d(TAG,"All variations are zero")
//                    }

                }

            }
        }


    }
}
class VariationDiffUtils : DiffUtil.ItemCallback<VariationsModel>() {
    override fun areItemsTheSame(
        oldItem: VariationsModel,
        newItem: VariationsModel
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: VariationsModel,
        newItem: VariationsModel
    ): Boolean {
        return oldItem == newItem
    }

}