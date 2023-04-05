package com.zingit.restaurant.views.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {

    lateinit var binding:FragmentHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_help, container, false)
        binding.apply {
            backArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }


}