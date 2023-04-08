package com.zingit.restaurant.views.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.CategoryAdapter
import com.zingit.restaurant.databinding.FragmentMenuBinding
import com.zingit.restaurant.viewModel.ExploreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MenuFragment : Fragment() {
    private  val TAG = "MenuFragment"
    private lateinit var binding: FragmentMenuBinding
    private val exploreViewModel: ExploreViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false)
        exploreViewModel.getMenuData()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    launch {
                        categoryAdapter = CategoryAdapter(requireContext())
                        exploreViewModel.categoryData.collect{
                            categoryAdapter.submitList(it.data)

                        }

                    }
                }

            }



            return binding.root
        }
    }


}