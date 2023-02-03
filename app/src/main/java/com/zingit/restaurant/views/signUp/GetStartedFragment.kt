package com.zingit.restaurant.views.signUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentGetStartedBinding


class GetStartedFragment : Fragment() {
    lateinit var binding: FragmentGetStartedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_get_started, container, false)
        binding.apply {
            getStarted.setOnClickListener {
                findNavController().navigate(R.id.action_getStartedFragment_to_loginSignUpFragment)
            }
        }
        return binding.root
    }


}