package com.zingit.restaurant.views.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ActiveOrderAdapter
import com.zingit.restaurant.adapter.PastOrderAdapter
import com.zingit.restaurant.databinding.FragmentViewPastOrderBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.utils.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint

class ViewPastOrderFragment : Fragment() {
    private  val TAG = "ViewPastOrderFragment"

    lateinit var binding: FragmentViewPastOrderBinding
    lateinit var gson: Gson
    lateinit var pastOrderAdapter: PastOrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_view_past_order, container, false)
        val data = arguments?.getString("orderModel")
        gson = Gson()
       val orderModel =  gson.fromJson(data,OrdersModel::class.java)
        Log.e(TAG, "onCreateView: $orderModel")
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            pastOrder = orderModel
            pastOrderAdapter = PastOrderAdapter(requireContext())
            itemRv.adapter= pastOrderAdapter
            pastOrderAdapter.submitList(orderModel.orderItems)
            backArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }




        return binding.root
    }

}