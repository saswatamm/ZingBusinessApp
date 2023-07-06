package com.zingit.restaurant.views.setting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.zingit.restaurant.adapter.LinkAdapter
import com.zingit.restaurant.adapter.QrVolumeAdapter
import com.zingit.restaurant.databinding.FragmentLinkVolumeBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.viewModel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LinkVolumeFragment : Fragment() {

    lateinit var binding: FragmentLinkVolumeBinding
    lateinit var linkAdapter: LinkAdapter
    private val TAG = "LinkedVolumeFragment"
    private val viewModel: TransactionViewModel by viewModels()
    var itemList: List<OrdersModel> = arrayListOf()
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_link_volume, container, false)
        viewModel.getEarningData()
        viewModel.getLinkedOrdersData()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            linkAdapter = LinkAdapter(requireContext()) {
                if (it != null) {
                    gson = Gson()
                    val json = gson.toJson(it)
                    val bundle = bundleOf("orderModel" to json)
                    findNavController().navigate(R.id.newOrderFragment, bundle)

                }


            }
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.orderLinkedData.collectLatest {
                            if (it.isNotEmpty()) {
                                itemList = it
                                linkVolumeRv.adapter = linkAdapter
                                Log.e(TAG, "onCreateView: ${itemList.size}")
                                linkAdapter.submitList(itemList)

                            }
                        }


                    }
                    launch {
                        viewModel.earningData.collect {
                            earningModel = it
                        }
                    }
                }
            }

        }

        return binding.root
    }

}