package com.zingit.restaurant.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zingit.restaurant.models.item.CategoryModel
import com.zingit.restaurant.models.item.CategoryState
import com.zingit.restaurant.models.item.ItemMenuState
import com.zingit.restaurant.models.resturant.RestaurantProfileState
import com.zingit.restaurant.repository.FirebaseRepository
import com.zingit.restaurant.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class ExploreViewModel @Inject
constructor(
    private var firebaseRepository: FirebaseRepository,
) : ViewModel() {
    private val _iteMenuData = MutableStateFlow(ItemMenuState())
    val iteMenuData: StateFlow<ItemMenuState> = _iteMenuData

    private val _categoryData = MutableStateFlow(CategoryState())
    val categoryData: StateFlow<CategoryState> = _categoryData
   var tempList:MutableList<CategoryModel> = mutableListOf()


    @JvmOverloads
    fun getMenuData(category:String?=null) {

        firebaseRepository.getMenuData().onEach {
            when (it) {
                is Resource.Loading -> {
                    _iteMenuData.value = ItemMenuState(isLoading = true)
                }
                is Resource.Error -> {
                    _iteMenuData.value = ItemMenuState(isLoading = false,error = it.message ?: "")
                    Log.d("In ExploreViewModels if error by FirebaseRepor func named getMenuData",it.message.toString())
                }
                is Resource.Success -> {

                    it.data?.forEachIndexed { index, itemMenuModel ->
                        tempList.add(CategoryModel(itemMenuModel.categoryName,itemMenuModel.itemImgUrl))
                    }
                    if(category==null){
                        val menuFinal = it.data?.filter {it1 -> it1.categoryName == it.data[0].categoryName
                          }?.toList()
                        Log.e("MenuFinal", "getMenuDatagg: $menuFinal", )
                        _iteMenuData.value = ItemMenuState(isLoading = false,data = menuFinal)
                    }else{
                        val menuFinal = it.data?.filter {it1 -> it1.categoryName == category
                        }?.toList()
                        Log.e("MenuFinal", "getMenuData: $menuFinal", )
                        _iteMenuData.value = ItemMenuState(isLoading = false, data = menuFinal)
                    }



                    _categoryData.value = CategoryState(data = tempList.distinctBy { it.categoryName }.toList())
                }
            }
        }.launchIn(viewModelScope)
    }


}