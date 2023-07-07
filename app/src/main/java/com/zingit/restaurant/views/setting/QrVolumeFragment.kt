package com.zingit.restaurant.views.setting

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.QrVolumeAdapter
import com.zingit.restaurant.databinding.FragmentQrVolumeBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.viewModel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class QrVolumeFragment : Fragment() {
    lateinit var binding: FragmentQrVolumeBinding
    lateinit var qrVolumeAdapter: QrVolumeAdapter
    private val TAG = "QrVolumeFragment"
    lateinit var gson: Gson

    private val viewModel: TransactionViewModel by viewModels()
    var itemList: List<OrdersModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_qr_volume, container, false)
        /*for(i in 1.. 100)
        {
            itemList.add(VolumeModel("88222",true,"100"))
        }*/


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

        Log.d("Calendar", year.toString() + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day)))

        viewModel.getEarningData(year.toString() + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day)))
        viewModel.getOrdersData(year.toString() + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day)))

        binding.dateValueTv.text =
            year.toString() + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day-1))

        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox

                binding.dateValueTv.setText(year.toString() + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d",(dayOfMonth)))
                viewModel.getEarningData(year.toString() + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d",(dayOfMonth)))
                viewModel.getOrdersData(year.toString() + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d",(dayOfMonth)))

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
            qrVolumeAdapter = QrVolumeAdapter(requireContext()) {
                if (it != null) {
                    gson = Gson()
                    val json = gson.toJson(it)
                    val bundle = bundleOf("orderModel" to json)
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
                        viewModel.orderActiveData.collectLatest {
                            if (it.isNotEmpty()) {
                                itemList = it
                                qrVolumeRv.adapter = qrVolumeAdapter
                                qrVolumeAdapter.submitList(itemList)

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




            return binding.root
        }
    }

}