package com.zingit.restaurant.views.order

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.util.Util
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ActiveOrderAdapter
import com.zingit.restaurant.adapter.InstantOrderAdapter
import com.zingit.restaurant.adapter.ViewPagerAdapter
import com.zingit.restaurant.databinding.FragmentOrdersBinding
import com.zingit.restaurant.models.PaymentModel
import com.zingit.restaurant.models.order.OrdersModel
import com.zingit.restaurant.utils.SlideInItemAnimator
import com.zingit.restaurant.utils.Utils
import com.zingit.restaurant.utils.Utils.hideKeyboard
import com.zingit.restaurant.utils.printer.AsyncBluetoothEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrint
import com.zingit.restaurant.utils.printer.AsyncEscPosPrinter
import com.zingit.restaurant.viewModel.OrdersViewModel
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import com.zingit.restaurant.views.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OrdersFragment : Fragment() {
    lateinit var binding: FragmentOrdersBinding
    lateinit var firestore: FirebaseFirestore
    private val orderViewModel: OrdersViewModel by viewModels()
    lateinit var query: Query
    lateinit var paymentModel: OrdersModel
    private val TAG = "OrdersFragment"
    lateinit var gson: Gson
    private val selectedDevice: BluetoothConnection? = null
    val PERMISSION_BLUETOOTH = 1
    val PERMISSION_BLUETOOTH_ADMIN = 2
    val PERMISSION_BLUETOOTH_CONNECT = 3
    val PERMISSION_BLUETOOTH_SCAN = 4

    var uniqueOrders = HashSet<String>() //To print only unique orders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.checkPermissions(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        firestore = FirebaseFirestore.getInstance()
        binding.viewmodel = orderViewModel


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var fragments = mutableListOf<Fragment>()
        fragments.add(ActiveFragment())
        fragments.add(HistoryFragment())
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, fragments)
        binding.viewpager.adapter = adapter
        val titles = mutableListOf("Active", "History")
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        binding.apply {

            searchView.setOnEditorActionListener { textView, i, keyEvent ->
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    loader.visibility = View.VISIBLE

                    firestore.collection("payment").whereEqualTo("orderNo", searchView.text.toString())
                        .whereEqualTo("outletID", Utils.getUserOutletId(requireContext()))
                        .whereGreaterThan("statusCode", 0).whereLessThan("statusCode", 3)
                        .addSnapshotListener { value, e ->
                            Log.e(TAG, "eror: ${e.toString()}", )
                            if (e == null) {
                                loader.visibility = View.GONE
                                Toast.makeText(requireContext(), "Order does not exist", Toast.LENGTH_SHORT).show()
                                view?.hideKeyboard()
                                binding.searchView.text.clear()
                                return@addSnapshotListener
                            }
                            if (value == null) {
                                Log.w(TAG, "Listen failed.", e)
                                loader.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "Order does not exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                                view?.hideKeyboard()
                                binding.searchView.text.clear()
                            } else {
                                val gson = Gson()

                                for (doc in value!!) {
                                    val finalValue = doc.toObject(OrdersModel::class.java)
                                    val json = gson.toJson(finalValue)
                                    loader.visibility = View.GONE
                                    val bundle = bundleOf("orderModel" to json)
                                    findNavController().navigate(
                                        R.id.action_ordersFragment_to_newOrderFragment,
                                        bundle
                                    )
                                    view?.hideKeyboard()
                                    binding.searchView.text.clear()
                                    break
                                }
                            }
                            view.hideKeyboard()
                        }
                    true
                } else {
                    false
                }
            }
            /*go.setOnClickListener {
                firestore.collection("payment").whereGreaterThan("statusCode",0).whereLessThan("statusCode",3).whereEqualTo("outletID","9i1Q3aRU8AiH0dUAZjko").get().addOnSuccessListener {
                    for (document in it) {
                        Log.e(TAG, "${document.id} => ${document.data.get("orderNo")}")
                        if (searchView.text.toString().trim()
                                .contains(document.data.get("orderNo").toString())
                        ) {
                            Log.e(TAG, "${document.id} => ${document.data.get("orderNo")}")
                            val gson = Gson()
                            loader.visibility = View.GONE
                            val finalValue = document.toObject(OrdersModel::class.java)
                            val json = gson.toJson(finalValue)
                            val bundle = bundleOf("orderModel" to json)
                            findNavController().navigate(
                                R.id.action_ordersFragment_to_newOrderFragment,
                                bundle
                            )
                            view?.hideKeyboard()
                            binding.searchView.text.clear()
                            break
                        }

                    }

                }

            }*/
            go.setOnClickListener {
                firestore.collection("payment").whereEqualTo("orderNo", searchView.text.toString())
                    .whereEqualTo("outletID", Utils.getUserOutletId(requireContext()))
                    .whereGreaterThan("statusCode", 0).whereLessThan("statusCode", 3)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            //Toast.makeText(requireContext(), "Order does not exist", Toast.LENGTH_SHORT).show()
                            view?.hideKeyboard()
                            binding.searchView.text.clear()
                            return@addSnapshotListener
                        }

                        if(value==null)
                        {
                            Log.w(TAG, "Listen failed.", e)
                            Toast.makeText(requireContext(), "Order does not exist", Toast.LENGTH_SHORT).show()
                            view?.hideKeyboard()
                            binding.searchView.text.clear()
                            return@addSnapshotListener
                        }
                        else {

                            val gson = Gson()

                            for (doc in value!!) {
                                val finalValue = doc.toObject(OrdersModel::class.java)
                                val json = gson.toJson(finalValue)
                                loader.visibility = View.GONE
                                val bundle = bundleOf("orderModel" to json)
                                findNavController().navigate(
                                    R.id.action_ordersFragment_to_newOrderFragment,
                                    bundle
                                )
                                view?.hideKeyboard()
                                binding.searchView.text.clear()
                                break
                            }
                        }
                    }
            }
            lifecycleScope.launch {
                launch {
                    orderViewModel.orderSearchData.collect {
                        Log.e(TAG, "onViewCreated: $it")
                    }
                }


            }
            Handler().postDelayed({
                query = firestore.collection("prod_order").whereEqualTo("restaurant.details.restaurant_id",Utils.getUserOutletId(requireContext())).whereEqualTo("zingDetails.status",1)
                query.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                        Log.e(TAG, "onCreateView: ${value!!.documents}")
                        if (error != null) {
                            Log.e(TAG, "fetchUsersData: ${error.message}")
                            return
                        }
                        for (i in value!!.documentChanges) {
                            Log.e(TAG, "fetchUsersData: ${i.document.data}")
                            when(i.type){
                                DocumentChange.Type.ADDED -> {
                                    if(!uniqueOrders.contains(i.document.data.get("paymentOrderID").toString()))
                                    {
                                        uniqueOrders.add(i.document.data.get("paymentOrderID").toString()) // Unique orders are added to prevent repetative printing
                                        paymentModel = i.document.toObject(OrdersModel::class.java)
                                        Log.e(TAG, "onEvent: ${paymentModel.orderItem?.details?.size}",)
//                                        Log.e(TAG, "onEvent: ${paymentModel.orderItem?.details?.size}",)
                                        printBluetooth(paymentModel, i.document.id)
                                    }
                                    else{
                                        Log.e(TAG,"eventPrinting: ${i.document.data.get("paymentOrderID").toString()}")   //Commented it out as already mentioned in HomeFragment
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    Log.e(TAG, "onEvent: ${i.document.data}")
                                }
                                DocumentChange.Type.REMOVED -> {
                                    Log.e(TAG, "onEvent: ${i.document.data}")
                                }
                            }
                        }
                    }
                })
            }, 5000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_BLUETOOTH, PERMISSION_BLUETOOTH_ADMIN, PERMISSION_BLUETOOTH_CONNECT, PERMISSION_BLUETOOTH_SCAN -> {

                }
            }
        }
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

//                            val sfDocRef = firestore.collection("payment").document(id)
                            val sfDocRef = firestore.collection("test_order").document(id)
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