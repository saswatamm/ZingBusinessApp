package com.zingit.restaurant.views.order


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.StructuredQuery.Order
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.CancelItemAdapter
import com.zingit.restaurant.adapter.CancelSpecificItemsAdapter
import com.zingit.restaurant.adapter.PastOrderAdapter
import com.zingit.restaurant.databinding.BottomCancelOrderBinding
import com.zingit.restaurant.databinding.BottomCancelSpecificItemBinding
import com.zingit.restaurant.databinding.FragmentNewOrderBinding
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.utils.Utils
import com.zingit.restaurant.utils.printer.AsyncBluetoothEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrinter
import com.zingit.restaurant.viewModel.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


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
    val arrayList1 = ArrayList<String>()
    val firestore = FirebaseFirestore.getInstance()
    private val selectedDevice: BluetoothConnection? = null
    val PERMISSION_BLUETOOTH = 1
    val PERMISSION_BLUETOOTH_ADMIN = 2
    val PERMISSION_BLUETOOTH_CONNECT = 3
    val PERMISSION_BLUETOOTH_SCAN = 4
    lateinit var printKOTBtn:MaterialButton
    lateinit var orderModel:OrdersModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

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

            pastOrderAdapter = PastOrderAdapter(requireContext())
            itemRv.adapter = pastOrderAdapter
            pastOrderAdapter.submitList(orderModel.orderItems)
            backArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        binding.printKOT.setOnClickListener {
            printBluetooth(orderModel,orderModel.id)
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
            Log.e(TAG, "cancel: $it", )
            if (it == getString(R.string.items_is)) {
                dialog.dismiss()
                itemsBottomSheet(ordersModel)
            }
        }
        binding.apply {
            recyclerView.adapter = cancelAdapter
            cancelAdapter.submitList(arrayList)
            keep.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


    fun itemsBottomSheet(ordersModel:OrdersModel){
        val binding: BottomCancelSpecificItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_cancel_specific_item, null, false)
        val d1 = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        d1.setCancelable(false)
        d1.setContentView(binding.root)
        d1.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            cancelSpecificItemsAdapter = CancelSpecificItemsAdapter(requireContext()) {
                Log.e(TAG, "cancel: $it", )
            }
            arrayList1.clear()
            recyclerView.adapter = cancelAdapter
            arrayList1.add("All Items")
            arrayList1.addAll(ordersModel.orderItems.map { it.itemName })
            cancelAdapter.submitList(arrayList1)
            keep.setOnClickListener {
                d1.dismiss()
            }
        }

        d1.show()
    }

    fun printBluetooth(ordersModel: OrdersModel, id: String) {


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                PERMISSION_BLUETOOTH_SCAN
            )
        } else {
            AsyncBluetoothEscPosPrint(
                requireContext(),
                object : AsyncEscPosPrint.OnPrintFinished() {
                    override fun onError(
                        asyncEscPosPrinter: AsyncEscPosPrinter?,
                        codeException: Int
                    ) {
                        Log.e(
                            "Async.OnPrintFinished",
                            "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                        )
                    }

                    override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                        Log.i(
                            "Async.OnPrintFinished",
                            "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                        )

                        try {

                            val sfDocRef = firestore.collection("payment").document(id)
                            Toast.makeText(
                                requireContext(),
                                "Print is finished ! $id",
                                Toast.LENGTH_SHORT
                            ).show()

                            firestore.runTransaction { transaction ->
                                transaction.update(sfDocRef, "statusCode", 2)
                            }.addOnSuccessListener {
                                Log.d(TAG, "Transaction success!")

                            }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Error $e",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error $e", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            )
                .execute(Utils.getAsyncEscPosPrinter(ordersModel, selectedDevice,requireContext()))
        }
    }


}