package com.zingit.restaurant.views.order

import android.Manifest
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
                    firestore.collection("payment").get().addOnSuccessListener {
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
                    view.hideKeyboard()
                    true
                } else {
                    false
                }
            }
            go.setOnClickListener {
                firestore.collection("payment").get().addOnSuccessListener {
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

            }
            lifecycleScope.launch {
                launch {
                    orderViewModel.orderSearchData.collect {
                        Log.e(TAG, "onViewCreated: $it")
                    }
                }
//                orderViewModel.orderPrintNew.collect {
//                    Log.e(TAG, "onViewCreated: $it")
//                    if (it.id !="" && it.statusCode !=2){
//                        Log.e(TAG, "statusCode: ${it.statusCode} ", )
//                        Log.e(TAG, "idd: ${it.id}")
//                        printBluetooth(it, it.id)
//                    }
//
//                }

            }
            Handler().postDelayed({
                query = firestore.collection("payment").whereEqualTo("outletID","9i1Q3aRU8AiH0dUAZjko").whereEqualTo("statusCode",1)
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
                                    Toast.makeText(requireContext(), i.document.data.get("paymentOrderID").toString(), Toast.LENGTH_SHORT).show()
                                    Log.e(TAG, "onEvent: ${i.document.data}")
                                    paymentModel = i.document.toObject(OrdersModel::class.java)
                                    Log.e(TAG, "onEvent: ${paymentModel.orderItems.size}", )

                                    printBluetooth(paymentModel, i.document.id)

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
                requireActivity(),
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                requireActivity(),
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
                requireActivity(),
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
                                .addOnFailureListener { e -> Toast.makeText(requireContext(),"Error $e",Toast.LENGTH_LONG).show() }
                        }catch (e:Exception){
                            Toast.makeText(requireContext(),"Error $e",Toast.LENGTH_LONG).show()
                            }
                    }
                }
            )
                .execute(Utils.getAsyncEscPosPrinter(ordersModel, selectedDevice))
        }
    }


}