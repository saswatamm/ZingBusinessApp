package com.zingit.restaurant.views.order

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.PastOrderAdapter
import com.zingit.restaurant.databinding.FragmentNewOrderBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.service.CountdownService
import com.zingit.restaurant.viewModel.OrderDetailsViewModel
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewOrderFragment : Fragment() {
    lateinit var binding:FragmentNewOrderBinding
    lateinit var gson: Gson
    lateinit var pastOrderAdapter: PastOrderAdapter
    private  val TAG = "NewOrderFragment"
    private val zingViewModel: OrderDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_new_order, container, false)
        gson = Gson()
        val data = arguments?.getString("orderModel")
        val orderModel =  gson.fromJson(data, OrdersModel::class.java)
        Log.e(TAG, "onCreateView: $orderModel")
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            pastOrder = orderModel
            viewModel = zingViewModel
            pastOrderAdapter = PastOrderAdapter(requireContext())
            itemRv.adapter= pastOrderAdapter
            pastOrderAdapter.submitList(orderModel.orderItems)
            backArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

  /*  fun startMyService() {
        val intent = Intent(requireContext(), CountdownService::class.java)
        requireActivity().startService(intent)
    }*/



}