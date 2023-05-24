package com.zingit.restaurant.views.home


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentHomeBinding
import com.zingit.restaurant.models.item.ItemMenuModel
import com.zingit.restaurant.utils.Utils
import com.zingit.restaurant.views.RootActivity
import kotlinx.coroutines.tasks.await


class HomeFragment : Fragment() {
    private  val TAG = "HomeFragment"

    lateinit var binding: FragmentHomeBinding
    lateinit var firebase:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        firebase = FirebaseFirestore.getInstance()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            switchButton.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {

//                    firebase.collection("outlet")
//                        .document(Utils.getUserOutletId(requireContext()).toString())
//                        .update("openStatus", "OPEN")

                    firebase.collection("test_restaurant")
                        .document(Utils.getUserOutletId(requireContext()).toString())
                        .update("active", 1)
                        statusOff.visibility = View.GONE
                        statusOn.visibility = View.VISIBLE



                } else {
//                    firebase.collection("outlet")
//                        .document(Utils.getUserOutletId(requireContext()).toString())
//                        .update("openStatus", "CLOSE")

                    firebase.collection("test_restaurant")
                        .document(Utils.getUserOutletId(requireContext()).toString())
                        .update("active", 0)
                    statusOff.visibility = View.VISIBLE
                    statusOn.visibility = View.GONE
                }
            }

            firebase.collection("test_restaurant")
                .document(Utils.getUserOutletId(requireContext()).toString())
                .get()
                .addOnSuccessListener {
                    loader.visibility = View.GONE
                    Log.e(TAG, "dataOpenClose: ${it.data}",)
                    if (it.exists()) {
                        Log.d("HomeFragment",""+ (it.data?.get("active")))
                        if (it.data?.get("active") == 1) {
                            switchButton.isChecked = true
                            statusOff.visibility = View.GONE
                            statusOn.visibility = View.VISIBLE
//                            Log.d("HomeFragment","value of active"+it.get("active"))
                        }
                        else {
                            statusOff.visibility = View.VISIBLE
                            statusOn.visibility = View.GONE
                            switchButton.isChecked = false
                        }
                    }
                }
//            firebase.collection("outlet")
//                .document(Utils.getUserOutletId(requireContext()).toString())
//                .get()
//                .addOnSuccessListener {
//                    loader.visibility = View.GONE
//                    Log.e(TAG, "dataOpenClose: ${it.data}",)
//                    if (it.exists()) {
//                        if (it.get("openStatus") == "OPEN") {
//                            switchButton.isChecked = true
//                            statusOff.visibility = View.GONE
//                            statusOn.visibility = View.VISIBLE
//                        } else {
//                            statusOff.visibility = View.VISIBLE
//                            statusOn.visibility = View.GONE
//                            switchButton.isChecked = false
//                        }
//                    }
//                }


//            Handler().postDelayed({
//                loader.visibility = View.VISIBLE
//
//
//
//            }, 2000)



            return binding.root
        }
    }






}