package com.zingit.restaurant.views.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ActiveOrderAdapter
import com.zingit.restaurant.adapter.QrVolumeAdapter
import com.zingit.restaurant.databinding.FragmentQrVolumeBinding
import com.zingit.restaurant.models.VolumeModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.assertThreadDoesntHoldLock


@AndroidEntryPoint
class QrVolumeFragment : Fragment() {
    lateinit var binding : FragmentQrVolumeBinding
    lateinit var qrVolumeAdapter: QrVolumeAdapter
    var itemList:ArrayList<VolumeModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_qr_volume, container, false)
        for(i in 1.. 100)
        {
            itemList.add(VolumeModel("88222",true,"100"))
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    qrVolumeAdapter= QrVolumeAdapter(itemList)
                    qrVolumeRv.adapter=qrVolumeAdapter

            }



            return binding.root
        }
    }

}