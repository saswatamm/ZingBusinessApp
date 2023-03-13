package com.zingit.restaurant.views.home

import android.content.Intent
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
import com.google.firebase.firestore.*
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentHomeBinding
import com.zingit.restaurant.utils.hideKeyboard
import com.zingit.restaurant.views.FirebaseNotificationActionActivity
import javax.inject.Inject


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var firestore: FirebaseFirestore
    private val TAG = "HomeFragment"
    lateinit var query: Query
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
            query = firestore.collection("payment").whereEqualTo("outletID","1cLAN8pKJcuyIML9g8Uz").whereEqualTo("statusCode",1)
            query.addSnapshotListener(object :EventListener<QuerySnapshot> {
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
/*
            firestore.collection("payment").whereEqualTo("outletID","1cLAN8pKJcuyIML9g8Uz").whereEqualTo("statusCode",1).addSnapshotListener { value, error ->
                Log.e(TAG, "onCreateView: ${value!!.documents}")
                if (error != null) {
                    Log.e(TAG, "fetchUsersData: ${error.message}")
                    return@addSnapshotListener
                }
                for (i in value!!.documents) {
                    Log.e(TAG, "fetchUsersData: ${i.data}")
                }
            }
*/


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
                                    view?.hideKeyboard()
                                    startActivity(Intent(context, FirebaseNotificationActionActivity::class.java).putExtra("id",count))
                                    binding.searchView.text.clear()
                                    break
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