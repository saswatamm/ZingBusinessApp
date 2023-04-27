package com.zingit.restaurant.views.home


import android.os.Bundle
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
import com.zingit.restaurant.utils.Utils


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
                Toast.makeText(requireContext(), "$isChecked", Toast.LENGTH_SHORT).show()
                if (isChecked) {
                    firebase.collection("outlet")
                        .document(Utils.getUserOutletId(requireContext()).toString())
                        .update("openStatus", "OPEN")
                    statusOff.visibility = View.GONE
                    statusOn.visibility = View.VISIBLE
                } else {
                    firebase.collection("outlet")
                        .document(Utils.getUserOutletId(requireContext()).toString())
                        .update("openStatus", "CLOSE")
                    statusOff.visibility = View.VISIBLE
                    statusOn.visibility = View.GONE
                }
            }


            firebase.collection("outlet")
                .document(Utils.getUserOutletId(requireContext()).toString())
                .get()
                .addOnSuccessListener {
                    Log.e(TAG, "onCreateView: ${it.data}", )
                    if (it.exists()) {
                        if (it.get("openStatus") == "OPEN") {
                            switchButton.isChecked = true
                            statusOff.visibility = View.GONE
                            statusOn.visibility = View.VISIBLE
                        } else {
                            statusOff.visibility = View.VISIBLE
                            statusOn.visibility = View.GONE
                            switchButton.isChecked = false
                        }
                    }
                }


//            if (switchButton.labelOff=="Close") {
//                firebase.collection("outlet")
//                    .document(Utils.getUserOutletId(requireContext()).toString())
//                    .update("openStatus", "CLOSE")
//                statusOff.visibility = View.VISIBLE
//                statusOn.visibility = View.GONE
//
//            }
//            if(switchButton.labelOn=="Open"){
//                firebase.collection("outlet")
//                    .document(Utils.getUserOutletId(requireContext()).toString())
//                    .update("openStatus", "OPEN")
//                statusOff.visibility = View.GONE
//                statusOn.visibility = View.VISIBLE
//            }

        }


        return binding.root
    }







}