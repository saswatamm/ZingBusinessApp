package com.zingit.restaurant.views.setting

import android.app.DatePickerDialog
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
import java.util.Calendar
import javax.xml.datatype.DatatypeConstants.MONTHS


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

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.dateValueTv.text=day.toString()+"."+(month+1).toString()+"."+year.toString()

        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            binding.dateValueTv.setText("" + dayOfMonth + "." + (monthOfYear+1) + "." + year)

        }, year, month, day)



        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    qrVolumeAdapter= QrVolumeAdapter(itemList)
                    qrVolumeRv.adapter=qrVolumeAdapter

                calendarIcon.setOnClickListener {
                    dpd.show()
                }

            }



            return binding.root
        }
    }

}