package com.zingit.restaurant.views.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.CategoryAdapter
import com.zingit.restaurant.adapter.MenuItemAdapter
import com.zingit.restaurant.adapter.VariationItemAdapter
import com.zingit.restaurant.databinding.FragmentMenuBinding
import com.zingit.restaurant.repository.FirebaseRepository
import com.zingit.restaurant.viewModel.ExploreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MenuFragment : Fragment() {
    private  val TAG = "MenuFragment"
    private lateinit var binding: FragmentMenuBinding
    private val exploreViewModel: ExploreViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var menuItemAdapter: MenuItemAdapter
//    private lateinit var firebaseRepository:FirebaseRepository
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
                        categoryAdapter = CategoryAdapter(requireContext(),0){
                            exploreViewModel.getMenuData(it.categoryName)
                            Log.d(TAG, "getmenudatfunc Category data : $it")
                        }
                        exploreViewModel.categoryData1.collect{
                            if(it.isLoading){
                                ll.visibility = View.GONE
                                loader.visibility = View.VISIBLE
                                requireActivity().window.setFlags(
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }else{

                                loader.visibility = View.GONE
                                ll.visibility = View.VISIBLE
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                            }
                            Log.e(TAG, "onCreateView Cat data: ${it.data}", )
                            categoryAdapter.submitList(it.data)
                            rvCategory.adapter = categoryAdapter
                            Log.d(TAG,".catdata :"+it.data.toString())
                        }
                    }
                    launch {
                        menuItemAdapter = MenuItemAdapter(requireContext())
                        exploreViewModel.iteMenuData.collect{
                            if(it.isLoading){
                                ll.visibility = View.GONE
                                loader.visibility = View.VISIBLE
                                requireActivity().window.setFlags(
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }else{

                                loader.visibility = View.GONE
                                ll.visibility = View.VISIBLE
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                            }
                            Log.e(TAG, "onCreateView: ${it.data}", )
                            menuItemAdapter.submitList(it.data)
                            rvProducts.adapter = menuItemAdapter
                            //EXPERIMENTAL
//                            firebaseRepository.getAddonGroupData(it.data?.get(0)!!.firebaseRestaurantId).collect(){
//                                Log.d(TAG,"Value of AddonGroupModel is:"+it.data)
//                            }
                            //EXPERIMENTAL
                        }
                    }
                }

            }
            return binding.root
        }
    }


}