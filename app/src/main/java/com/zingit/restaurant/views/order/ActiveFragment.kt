package com.zingit.restaurant.views.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import com.zingit.restaurant.adapter.CategoryAdapter
import com.zingit.restaurant.adapter.MenuItemAdapter
import com.zingit.restaurant.databinding.FragmentActiveBinding
import com.zingit.restaurant.viewModel.ExploreViewModel
import com.zingit.restaurant.viewModel.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch


@AndroidEntryPoint

class ActiveFragment : Fragment() {

    lateinit var binding: FragmentActiveBinding
    private val TAG = "ActiveFragment"

    private val orderViewModel: OrdersViewModel by viewModels()
    lateinit var gson: Gson

    private lateinit var orderAdapter: ActiveOrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_active, container, false)
        orderViewModel.getOrdersData()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                orderAdapter = ActiveOrderAdapter(requireContext()) {
                    if (it != null) {
                        gson = Gson()
                        val json = gson.toJson(it)
                        val bundle = bundleOf("orderModel" to json)
                        findNavController().navigate(
                            R.id.action_ordersFragment_to_newOrderFragment,
                            bundle
                        )

                    }


                }

               val loading =  async {
                    orderViewModel.loading.collect {
                        if (it) {
                            tagline.visibility = View.GONE
                            loader.visibility = View.VISIBLE
                            requireActivity().window.setFlags(
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            )

                        } else {
                            loader.visibility = View.GONE
                            tagline.visibility =
                                if (orderViewModel.orderActiveData.value.isEmpty()) View.VISIBLE else View.GONE
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }


                    }
                }

              val data =   async {
                  orderViewModel.orderActiveData.collect {
                      if (!it.isEmpty()) {
                          tagline.visibility = View.GONE

                          activeRv.adapter = orderAdapter
                          orderAdapter.submitList(it)
                      } else {
                          tagline.visibility = View.VISIBLE

                      }

                  }
              }

                    awaitAll(loading,data)



            }






        }



        return binding.root
    }

}


