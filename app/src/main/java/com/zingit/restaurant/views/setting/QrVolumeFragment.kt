package com.zingit.restaurant.views.setting

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.QrVolumeAdapter
import com.zingit.restaurant.databinding.FragmentQrVolumeBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.utils.Utils
import com.zingit.restaurant.viewModel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class QrVolumeFragment : Fragment() {
    lateinit var binding: FragmentQrVolumeBinding
    lateinit var qrVolumeAdapter: QrVolumeAdapter
    lateinit var firestore: FirebaseFirestore
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
        firestore = FirebaseFirestore.getInstance()
      
        viewModel.getEarningData()
        viewModel.getOrdersData()


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

        binding.dateValueTv.text =
            day.toString() + "." + (month + 1).toString() + "." + year.toString()

        val dpd = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                val formattedMonth = (monthOfYear + 1).toString().padStart(2, '0')
                val formattedDay = dayOfMonth.toString().padStart(2, '0')
                val date = "$year-$formattedMonth-$formattedDay"
                callDate(date)
                binding.dateValueTv.setText("" + dayOfMonth + "." + (monthOfYear + 1) + "." + year)

            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = minCalendar.timeInMillis
        dpd.datePicker.maxDate = System.currentTimeMillis()
        
        



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
                    findNavController().navigate(
                        R.id.newOrderFragment,
                        bundle
                    )

                }
            }

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.orderActiveData.collectLatest {
                            Toast.makeText(context, "${it.toString()}", Toast.LENGTH_SHORT).show()
                            itemList = it
                            qrVolumeRv.adapter = qrVolumeAdapter
                            qrVolumeAdapter.submitList(itemList)


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
    
    fun callDate(date:String){
        itemList = arrayListOf()
        Toast.makeText(context, "$date", Toast.LENGTH_SHORT).show()

        firestore.collection("prod_order")
            .whereEqualTo("restaurant.details.restaurant_id", Utils.getMenuSharingCode(requireContext()))
            .whereEqualTo("zingDetails.qrScanned",true)
            .whereEqualTo("order.details.preorder_date",date)
            .addSnapshotListener { value, error ->
                if (error != null) {
                   
                }
                if (value != null) {
                    var orderModel = ArrayList<OrdersModel>()
                    val valueIterator = value.documents.iterator()
                    while (valueIterator.hasNext()) {
                        var valueIteratorObject = valueIterator.next()
                        var orderModelObject = valueIteratorObject.toObject(OrdersModel::class.java)
                        if (orderModelObject != null) {
                            orderModelObject.zingDetails?.id = valueIteratorObject.id
                        }
                        if (orderModelObject != null) {
                            orderModel.add(orderModelObject)
                        }
                    }
                    var list = orderModel.sortedByDescending { it.zingDetails?.placedTime }
                    itemList = list
                    Log.e("SortedDateList", "orderData is:$orderModel")
                    qrVolumeAdapter.submitList(itemList)
                    qrVolumeAdapter.notifyDataSetChanged()
                }
            }
        
    }

}