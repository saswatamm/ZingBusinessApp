package com.zingit.restaurant.views.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ActiveOrderAdapter
import com.zingit.restaurant.adapter.CategoryAdapter
import com.zingit.restaurant.adapter.MenuItemAdapter
import com.zingit.restaurant.databinding.FragmentActiveBinding
import com.zingit.restaurant.viewModel.ExploreViewModel
import com.zingit.restaurant.viewModel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint

class ActiveFragment : Fragment() {

    lateinit var binding: FragmentActiveBinding

    private val orderViewModel: OrdersViewModel by viewModels()

    private lateinit var orderAdapter: ActiveOrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel.getOrdersData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_active, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    launch {
                        orderAdapter = ActiveOrderAdapter(requireContext(),){

                        }
                        orderViewModel.orderActiveData.collect{
                            activeRv.adapter = orderAdapter
                            orderAdapter.submitList(it.data)

                        }
                    }
                }

            }
            return binding.root
        }


        return binding.root
    }


}