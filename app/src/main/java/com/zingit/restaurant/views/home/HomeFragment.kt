package com.zingit.restaurant.views.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var firestore: FirebaseFirestore
    private val TAG = "HomeFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()
        binding.apply {
            searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.d("checkDatas", "brfiore: ${p0.toString()}")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.d("checkDatas", "onTextChanged: ${p0.toString()}")
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().trim().isNotEmpty()) {
                        firestore.collection("payment").get().addOnSuccessListener {
                            var count:String ?=null
                            for (document in it) {
                                Log.e(TAG, "${document.id} => ${document.data.get("paymentOrderID")}")
                                if (p0.toString().trim().contains(document.data.get("paymentOrderID").toString())){
                                    Log.e(TAG, "${document.id} => ${document.data.get("paymentOrderID")}")
                                    count= document.id
                                    Toast.makeText(context, document.id, Toast.LENGTH_SHORT).show()
                                    break
                                }

                            }
                            if(count !=null){
                                Toast.makeText(context, count, Toast.LENGTH_SHORT).show()
                                firestore.collection("payment").document(count).get().addOnSuccessListener {
                                    for (i in it.data!!.keys) {
                                        Log.e(TAG, "${i}")


                                    }
                                }

                            }
                        }
                    }
                }
            })


        }



        return binding.root
    }


}