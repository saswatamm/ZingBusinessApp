package com.zingit.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zingit.restaurant.databinding.VolumeItemBinding
import com.zingit.restaurant.models.VolumeModel
import com.zingit.restaurant.models.order.OrdersModel

class QrVolumeAdapter(private var itemsList:ArrayList<VolumeModel>):RecyclerView.Adapter<QrVolumeAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding:VolumeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(volumeModel: VolumeModel) {

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QrVolumeAdapter.MyViewHolder {
        val binding =
            VolumeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val item=itemsList[position]
    }

    override fun getItemCount(): Int {
    return itemsList.size
    }
}