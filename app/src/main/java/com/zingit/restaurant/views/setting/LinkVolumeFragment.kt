package com.zingit.restaurant.views.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentLinkVolumeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LinkVolumeFragment : Fragment() {

    lateinit var binding: FragmentLinkVolumeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_link_volume, container, false)

        return binding.root
    }

}