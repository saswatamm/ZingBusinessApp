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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.CancelItemAdapter
import com.zingit.restaurant.adapter.CancelSpecificItemsAdapter
import com.zingit.restaurant.adapter.PastOrderAdapter
import com.zingit.restaurant.databinding.BottomCancelOrderBinding
import com.zingit.restaurant.databinding.BottomSheetPrinterBinding
import com.zingit.restaurant.databinding.FragmentNewOrderBinding
import com.zingit.restaurant.models.item.CancelModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.service.CountdownService
import com.zingit.restaurant.viewModel.OrderDetailsViewModel
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewOrderFragment : Fragment() {
    lateinit var binding: FragmentNewOrderBinding
    lateinit var gson: Gson
    lateinit var pastOrderAdapter: PastOrderAdapter
    private val TAG = "NewOrderFragment"
    lateinit var cancelAdapter: CancelItemAdapter
    lateinit var cancelSpecificItemsAdapter: CancelSpecificItemsAdapter
    private val zingViewModel: OrderDetailsViewModel by viewModels()
    val arrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_order, container, false)
        gson = Gson()
        val data = arguments?.getString("orderModel")
        val orderModel = gson.fromJson(data, OrdersModel::class.java)
        Log.e(TAG, "onCreateView: $orderModel")
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            pastOrder = orderModel
            viewModel = zingViewModel
            val tempArray = arrayOf(
                getString(R.string.items_is),
                getString(R.string.too_order),
                getString(R.string.delivery),
                getString(R.string.not_accept)
            )
            arrayList.addAll(tempArray)
            rejectBtn.setOnClickListener {
                cancel()
            }

            pastOrderAdapter = PastOrderAdapter(requireContext())
            itemRv.adapter = pastOrderAdapter
            pastOrderAdapter.submitList(orderModel.orderItems)
            backArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

    fun cancel() {
        val binding: BottomCancelOrderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_cancel_order,
            null,
            false
        )
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        cancelAdapter = CancelItemAdapter(requireContext()) {
            if (it == getString(R.string.items_is)) {
                val binding: BottomCancelOrderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.bottom_cancel_order,
                    null,
                    false
                )
                cancelSpecificItemsAdapter = CancelSpecificItemsAdapter(requireContext()) {
                    binding.apply {
                        recyclerView.adapter = cancelAdapter
                        cancelAdapter.submitList(arrayList)
                        keep.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                    val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
                    dialog.setCancelable(false)
                    dialog.setContentView(binding.root)
                    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    dialog.show()
                }




            }
        }
        binding.apply {
            recyclerView.adapter = cancelAdapter
            Log.e(TAG, "cancel: $arrayList")
            cancelAdapter.submitList(arrayList)
            keep.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


}