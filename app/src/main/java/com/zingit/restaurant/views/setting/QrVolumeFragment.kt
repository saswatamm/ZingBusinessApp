package com.zingit.restaurant.views.setting

import android.app.DatePickerDialog
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
        viewModel.getEarningData()
        viewModel.getOrdersData()


        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.dateValueTv.text =
            day.toString() + "." + (month + 1).toString() + "." + year.toString()

        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.dateValueTv.setText("" + dayOfMonth + "." + (monthOfYear + 1) + "." + year)

            },
            year,
            month,
            day
        )



        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            qrVolumeAdapter = QrVolumeAdapter(requireContext())
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