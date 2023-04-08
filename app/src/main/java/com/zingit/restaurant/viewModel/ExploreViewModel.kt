package com.zingit.restaurant.viewModel

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


    fun getMenuData() {
        firebaseRepository.getMenuData().onEach {
            when (it) {
                is Resource.Loading -> {
                    _iteMenuData.value = ItemMenuState(isLoading = true)
                }
                is Resource.Error -> {
                    _iteMenuData.value = ItemMenuState(error = it.message ?: "")
                }
                is Resource.Success -> {
                    _iteMenuData.value = ItemMenuState(data = it.data)
                    it.data?.forEachIndexed { index, itemMenuModel ->
                        tempList.add(CategoryModel(itemMenuModel.category,itemMenuModel.itemImage))

                    }
                    _categoryData.value = CategoryState(data = tempList)


                }
            }
        }.launchIn(viewModelScope)
    }


}