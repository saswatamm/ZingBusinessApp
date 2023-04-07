package com.zingit.restaurant.views.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentMenuBinding
import com.zingit.restaurant.viewModel.ExploreViewModel
import com.zingit.restaurant.viewModel.RestaurantProfileViewModel
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class MenuFragment : Fragment() {
    private  val TAG = "MenuFragment"
    lateinit var binding: FragmentMenuBinding
    private val exploreViewModel: ExploreViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_menu, container, false)
        exploreViewModel.getMenuData()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewLifecycleOwner.lifecycle.coroutineScope.launchWhenCreated{
                exploreViewModel.iteMenuData.collect {
                    Log.e(TAG, "onCreateView: ${it.data?.size}", )
                   
     
                }
            }
        }
        
        
        return binding.root
    }


}