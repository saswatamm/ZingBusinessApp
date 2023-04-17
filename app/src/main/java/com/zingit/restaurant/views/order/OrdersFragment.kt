package com.zingit.restaurant.views.order

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ActiveOrderAdapter
import com.zingit.restaurant.adapter.InstantOrderAdapter
import com.zingit.restaurant.adapter.ViewPagerAdapter
import com.zingit.restaurant.databinding.FragmentOrdersBinding
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
                            if (searchView.text.toString().trim().contains(document.data.get("orderNo").toString())){
                                Log.e(TAG, "${document.id} => ${document.data.get("orderNo")}")
                                val gson = Gson()
                                loader.visibility = View.GONE
                                val finalValue = document.toObject(OrdersModel::class.java)
                                val json = gson.toJson(finalValue)
                                val bundle = bundleOf("orderModel" to json)
                                findNavController().navigate(R.id.action_ordersFragment_to_newOrderFragment,bundle)
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
           lifecycleScope.launch {
               launch {
                   orderViewModel.orderSearchData.collect{
                       Log.e(TAG, "onViewCreated: $it", )
                   }
               }
               orderViewModel.orderPrintNew.collect{
                   Log.e(TAG, "onViewCreated: $it", )
                   printBluetooth(it,it.id)
               }

           }





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
                PERMISSION_BLUETOOTH, PERMISSION_BLUETOOTH_ADMIN, PERMISSION_BLUETOOTH_CONNECT,PERMISSION_BLUETOOTH_SCAN -> {

                }
            }
        }
    }

    fun printBluetooth(ordersModel: OrdersModel,id:String) {
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
                    override fun onError(asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int) {
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
                        firestore.collection("payment").document(id).update("statusCode",2)
                    }
                }
            )
                .execute(Utils.getAsyncEscPosPrinter(ordersModel,selectedDevice))
        }
    }




}