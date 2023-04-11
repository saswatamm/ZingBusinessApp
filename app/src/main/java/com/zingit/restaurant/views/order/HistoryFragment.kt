package com.zingit.restaurant.views.order

import android.os.Bundle
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
import com.zingit.restaurant.adapter.HistoryAdapter
import com.zingit.restaurant.databinding.FragmentHistroyBinding
import com.zingit.restaurant.viewModel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHistroyBinding
    private val orderViewModel: OrdersViewModel by viewModels()
    private lateinit var orderHistoryAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_histroy, container, false)

        orderViewModel.getOrderHistory()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    launch {
                        orderHistoryAdapter = HistoryAdapter(requireContext(),){

                        }
                        orderViewModel.orderHistoryData.collect{

                            if(it.isLoading){
                                tagline.visibility = View.GONE
                                loader.visibility = View.VISIBLE
                                requireActivity().window.setFlags(
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }else{
                                loader.visibility = View.GONE
                                tagline.visibility = View.GONE
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                            historyRv.adapter = orderHistoryAdapter
                            orderHistoryAdapter.submitList(it.data)

                        }
                    }
                }

            }
            return binding.root
        }



    }


}