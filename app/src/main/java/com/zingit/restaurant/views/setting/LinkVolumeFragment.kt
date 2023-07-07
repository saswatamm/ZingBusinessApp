package com.zingit.restaurant.views.setting

import android.app.DatePickerDialog
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
import java.util.Calendar

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

        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, -1);
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val currentYear = Calendar.getInstance()[Calendar.YEAR]
        val minCalendar = Calendar.getInstance()
        minCalendar[Calendar.YEAR] = currentYear
        minCalendar[Calendar.MONTH] = Calendar.JANUARY
        minCalendar[Calendar.DAY_OF_MONTH] = 1

        viewModel.getEarningData(year.toString() + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day)))

        viewModel.getLinkedOrdersData(year.toString() + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day)))


        binding.dateValueTv.text =
            year.toString() + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day))

        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.dateValueTv.setText(year.toString() + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d",(dayOfMonth)))
                viewModel.getEarningData(year.toString() + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d",(dayOfMonth)))
                viewModel.getLinkedOrdersData(year.toString() + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d",(dayOfMonth)))

            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = minCalendar.timeInMillis
        dpd.datePicker.maxDate = (System.currentTimeMillis()-86400000);


        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            calendarIcon.setOnClickListener {
                dpd.show()
            }
            linkAdapter = LinkAdapter(requireContext()) {
                if (it != null) {
                    gson = Gson()
                    val json = gson.toJson(it)
                    if(it.zingDetails?.status=="5"){
                        val bundle = bundleOf("orderModel" to json)
                        findNavController().navigate(R.id.viewPastOrderFragment, bundle)
                    }
                    else{
                        val bundle = bundleOf("orderModel" to json)
                        findNavController().navigate(R.id.newOrderFragment, bundle)
                    }
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