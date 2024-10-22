package com.zingit.restaurant.views.order


import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.CancelItemAdapter
import com.zingit.restaurant.adapter.CancelSpecificItemsAdapter
import com.zingit.restaurant.adapter.PastOrderAdapter
import com.zingit.restaurant.databinding.BottomCancelOrderBinding
import com.zingit.restaurant.databinding.BottomCancelSpecificItemBinding
import com.zingit.restaurant.databinding.FragmentNewOrderBinding
import com.zingit.restaurant.models.item.CancelItemModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.models.refund.PhonePeReq
import com.zingit.restaurant.viewModel.OrderDetailsViewModel
import com.zingit.restaurant.views.RootActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import java.util.concurrent.TimeUnit


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
    private val cancelItemModel = ArrayList<CancelItemModel>()
    val cancelItemFinalList = ArrayList<CancelItemModel>()
    val firestore = FirebaseFirestore.getInstance()
    var fullBool = false
    var partialBool = false

    companion object {
        const val PERMISSION_BLUETOOTH = 1
        const val PERMISSION_BLUETOOTH_ADMIN = 2
        const val PERMISSION_BLUETOOTH_CONNECT = 3
        const val PERMISSION_BLUETOOTH_SCAN = 4
    }

    lateinit var orderModel: OrdersModel


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_order, container, false)

        gson = Gson()
        val data = arguments?.getString("orderModel")
        orderModel = gson.fromJson(data, OrdersModel::class.java)
        Log.e(TAG, "onCreateView: $orderModel")
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            pastOrder = orderModel
            viewModel = zingViewModel

            zingViewModel.whatsappPrepared.observe(viewLifecycleOwner){
                if (it.status == "Success") {
                    Toast.makeText(requireContext(), "Order Prepared", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }

            zingViewModel.whatsappDeniedData.observe(viewLifecycleOwner) {
                if (it.status == "Success") {
                    Toast.makeText(requireContext(), "Refund Initiated", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }




            arrayList.clear()
            val tempArray = arrayOf(
                getString(R.string.items_is),
                getString(R.string.too_order),
                getString(R.string.delivery),
                getString(R.string.not_accept)
            )
            arrayList.addAll(tempArray)
            rejectBtn.setOnClickListener {
                cancel(orderModel)
            }

            /*
            checking the timer --->PlaceTime Order
            Below Function
            */
            countDownTimer(rejectBtn)
            pastOrderAdapter = PastOrderAdapter(requireContext())
            itemRv.adapter = pastOrderAdapter
            pastOrderAdapter.submitList(orderModel.orderItem?.details)
            backArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }


        binding.printKOT.setOnClickListener {
            Log.d(TAG, "SelectedDevice is" + RootActivity().selectedDevice.toString())
            RootActivity().printBluetooth(
                requireContext(),
                requireActivity(),
                orderModel,
                orderModel.zingDetails?.id!!
            )


        }

        return binding.root
    }


    fun cancel(ordersModel: OrdersModel) {
        val binding: BottomCancelOrderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_cancel_order,
            null,
            false
        )
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        cancelAdapter = CancelItemAdapter(requireContext()) {
//            if (it == getString(R.string.items_is)) {
//                dialog.dismiss()
//                itemsBottomSheet(ordersModel)
//            }
        }

        binding.apply {
            recyclerView.adapter = cancelAdapter
            cancelAdapter.submitList(arrayList)
            radio.setOnCheckedChangeListener { group, checkedId ->
                // handle radio button checked state change
                when (checkedId) {
                    R.id.check_full_order -> {
                        // handle radio button 1 checked
                        fullBool = true
                        partialBool = false
                    }

                    R.id.check_a_particular_item -> {
                        // handle radio button 2 checked
                        fullBool = false
                        partialBool = true
                    }
                }

            }
            keep.setOnClickListener {
                dialog.dismiss()
            }
            close.setOnClickListener {
                dialog.dismiss()
            }
            cancelRefund.setOnClickListener {
                dialog.dismiss()
                itemsBottomSheet(ordersModel)

            }


        }

        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


    fun itemsBottomSheet(ordersModel: OrdersModel) {
        val binding: BottomCancelSpecificItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_cancel_specific_item,
            null,
            false
        )
        val d1 = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        d1.setCancelable(false)
        d1.setContentView(binding.root)
        d1.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            cancelSpecificItemsAdapter = CancelSpecificItemsAdapter(requireContext()) {
                if (it.isChecked) {
                    cancelItemFinalList.add(it)
                } else {
                    cancelItemFinalList.remove(it)
                }

            }

            cancelItemModel.clear()
            recyclerView.adapter = cancelSpecificItemsAdapter
//          cancelItemModel.add(CancelItemModel("All Items", "0", false))
            cancelItemModel.addAll(ordersModel.orderItem!!.details.map {
                CancelItemModel(
                    it.name,
                    it.id,
                    false
                )
            })
            cancelSpecificItemsAdapter.submitList(cancelItemModel)
            cancelRefund.setOnClickListener {
                if (cancelItemFinalList.isNotEmpty()) {
                    for (i in 0 until cancelItemFinalList.size) {
                        firestore.collection("prod_menu")
                            .document(cancelItemFinalList.get(i).itemId)
                            .update("active", "0")
                    }
                    var total = orderModel.order?.details?.total?.toDouble()?.times(100)
                    orderModel.order?.details?.let { it1 ->
                        ordersModel.restaurant?.details?.let { it2 ->
                                    zingViewModel.whatsappMultiOperationDenied(
                                        ordersModel,
                                        PhonePeReq(
                                            orderModel.zingDetails?.userID!!,
                                            orderModel.zingDetails?.paymentOrderId!!,
                                            UUID.randomUUID().toString(),
                                            total.toString(),
                                            "zingnow.in"
                                        )
                                    )


                        }
                    }
                    d1.dismiss()


                } else {
                    Toast.makeText(requireContext(), "Please Select Item", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            keep.setOnClickListener {
                d1.dismiss()
            }
            close.setOnClickListener {
                d1.dismiss()
            }

        }

        d1.show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun countDownTimer(rejectBtn: MaterialButton) {
        val targetDuration = Duration.ofMinutes(5)
        val time = orderModel.order?.details!!.createdOn.substringAfter(" ")
        val date = orderModel.order?.details!!.createdOn.substringBefore(" ")

        val dateTime = date + "T" + time
        val ldt = LocalDateTime.parse(dateTime)
        val givenTime = ldt.atZone(ZoneId.systemDefault()).toInstant()
        Log.d(TAG, givenTime.toString())
        val targetTime = givenTime.plus(targetDuration)
        val currentTime = Instant.now()
        val remainingDuration = Duration.between(currentTime, targetTime)
        if (remainingDuration.isNegative || remainingDuration.isZero) {
            rejectBtn.text = getString(R.string.reject_order)
            rejectBtn.isEnabled = true
            rejectBtn.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.textGrey
                )
            )
        } else {
            val countDownTimer = @RequiresApi(Build.VERSION_CODES.O)
            object : CountDownTimer(remainingDuration.toMillis(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Calculate the remaining time as a Duration
                    val text = "Reject Order ${
                        String.format(
                            "(%02d:%02d)",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                        )
                    }"

                    rejectBtn.isEnabled = true
                    // Display the remaining time in a TextView (replace "textView" with your own TextView)
                    rejectBtn.text = text
                    rejectBtn.background.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOnPrimary
                        )
                    )

                }

                override fun onFinish() {
                    // The timer has finished, do something here...
                    zingViewModel.whatsappAccepted(
                        orderModel.customer?.details!!.phone,
                        orderModel.order?.details!!.orderId,
                        orderModel.restaurant?.details!!.restaurant_name,
                        orderModel.customer?.details!!.name,
                        "15"
                    )
                    rejectBtn.isEnabled = false
                    rejectBtn.text = getString(R.string.reject_order)
                    rejectBtn.background.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.textGrey
                        )
                    )
                }
            }
            // Start the countdown timer
            countDownTimer.start()


        }


    }


}