package com.zingit.restaurant.views.order

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ActiveOrderAdapter
import com.zingit.restaurant.adapter.HistoryAdapter
import com.zingit.restaurant.databinding.FragmentHistroyBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.viewModel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistroyBinding
    private val orderViewModel: OrdersViewModel by viewModels()
    private lateinit var orderHistoryAdapter: HistoryAdapter
    private  val TAG = "HistoryFragment"
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_histroy, container, false)
        val data = arguments?.getString("orderModel")
        gson = Gson()
        val orderModel =  gson.fromJson(data, OrdersModel::class.java)

        orderViewModel.getOrderHistory()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                orderHistoryAdapter = HistoryAdapter(requireContext()) {
                    gson = Gson()
                    val json = gson.toJson(it)
                    val bundle = bundleOf("orderModel" to json)
                    findNavController().navigate(
                        R.id.action_ordersFragment_to_viewPastOrderFragment,
                        bundle
                    )

                }
                orderViewModel.orderHistoryData.collect {
                    loader.visibility = View.VISIBLE
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    if (it.isEmpty()) {
                        tagline.visibility = View.GONE
                        loader.visibility = View.GONE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    } else {
                        loader.visibility = View.GONE
                        tagline.visibility = View.GONE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        historyRv.adapter = orderHistoryAdapter
                        orderHistoryAdapter.submitList(it)
                    }


                }
            }
            return binding.root
        }

    }

}