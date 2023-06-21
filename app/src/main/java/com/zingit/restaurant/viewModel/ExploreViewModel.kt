package com.zingit.restaurant.viewModel

import android.content.ClipData.Item
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

//    private val _categoryData = MutableStateFlow(CategoryState())
//    val categoryData: StateFlow<CategoryState> = _categoryData
   var tempList:MutableList<CategoryModel> = mutableListOf()
//My new set of variables
    private val _categoryData1 = MutableStateFlow(CategoryState())
    val categoryData1: StateFlow<CategoryState> = _categoryData1
    var tempList1:MutableList<CategoryModel> = mutableListOf()


    @JvmOverloads
    fun getMenuData(category:String?=null) {

        firebaseRepository.getCategoryData().onEach {
            when(it)
            {
                is Resource.Loading -> {
                    _categoryData1.value= CategoryState(isLoading=true)
                }
                is Resource.Error -> {
                    _categoryData1.value= CategoryState(isLoading = false, error = it.message?:"")
                    Log.d("In ExploreViewModels error by FirebaseRepo func getCategoryData",it.message.toString())
                }
                is Resource.Success -> {

                    it.data?.forEachIndexed {index, categoryModel->
                        tempList1.add(categoryModel)
                    }
                    _categoryData1.value = CategoryState(data = tempList1.distinctBy { it.categoryName }.toList())
                    Log.d("EXpViewModel","_categoryData.value="+_categoryData1.value.toString())

                    firebaseRepository.getMenuData().onEach {
                        when (it) {
                            is Resource.Loading -> {
                                _iteMenuData.value = ItemMenuState(isLoading = true)
                            }
                            is Resource.Error -> {
                                _iteMenuData.value = ItemMenuState(isLoading = false,error = it.message ?: "")
                                Log.d("In ExploreViewModels if error by FirebaseRepo func named getMenuData",it.message.toString())
                            }
                            is Resource.Success -> {

//                    it.data?.forEachIndexed { index, itemMenuModel ->
//                        tempList.add(CategoryModel(itemMenuModel.categoryName,itemMenuModel.itemImgUrl))
//                        Log.d("EXpViewModel","categoryModel:"+tempList.toString())
//                    }
                                if(category==null){
                                    val menuFinal = it.data?.filter {it1 -> it1.categoryName == tempList1[0].categoryName   //Here, what if the above
                                    }?.toList()                                                                           //function, that populates templist1
                                    Log.e("MenuFinal", "getMenuDataNoCat: $menuFinal", )                           //,although is in flow, but exectues after this function?
                                    _iteMenuData.value = ItemMenuState(isLoading = false,data = menuFinal)
                                }
                                else{
                                    val menuFinal = it.data?.filter {it1 -> it1.categoryName == category
                                    }?.toList()
                                    Log.e("MenuFinal", "getMenuData: $menuFinal", )
                                    _iteMenuData.value = ItemMenuState(isLoading = false, data = menuFinal)
                                }
//                                //EXPERIMENTAL
//                                    firebaseRepository.getAddonGroupData(it.data?.get(0)!!.firebaseRestaurantId).collect{
//                                        Log.d("ExploreViewModel","Value of AddonGroupModel is:"+it.data)
//                                    }

                                //EXPERIMENTAL

//                    _categoryData.value = CategoryState(data = tempList1.distinctBy { it.categoryName }.toList())
//                    Log.d("EXpViewModel","_categoryData.value="+_categoryData.value.toString())
                            }
                        }
                    }.launchIn(viewModelScope)
                }
            }
        }.launchIn(viewModelScope)


//        firebaseRepository.getMenuData().onEach {
//            when (it) {
//                is Resource.Loading -> {
//                    _iteMenuData.value = ItemMenuState(isLoading = true)
//                }
//                is Resource.Error -> {
//                    _iteMenuData.value = ItemMenuState(isLoading = false,error = it.message ?: "")
//                    Log.d("In ExploreViewModels if error by FirebaseRepo func named getMenuData",it.message.toString())
//                }
//                is Resource.Success -> {
//
////                    it.data?.forEachIndexed { index, itemMenuModel ->
////                        tempList.add(CategoryModel(itemMenuModel.categoryName,itemMenuModel.itemImgUrl))
////                        Log.d("EXpViewModel","categoryModel:"+tempList.toString())
////                    }
//                    if(category==null){
//                        val menuFinal = it.data?.filter {it1 -> it1.categoryName == tempList1[0].categoryName   //Here, what if the above
//                          }?.toList()                                                                           //function, that populates templist1
//                        Log.e("MenuFinal", "getMenuDataNoCat: $menuFinal", )                           //,although is in flow, but exectues after this function?
//                        _iteMenuData.value = ItemMenuState(isLoading = false,data = menuFinal)
//                    }
//                    else{
//                        val menuFinal = it.data?.filter {it1 -> it1.categoryName == category
//                        }?.toList()
//                        Log.e("MenuFinal", "getMenuData: $menuFinal", )
//                        _iteMenuData.value = ItemMenuState(isLoading = false, data = menuFinal)
//                    }
//
//
////                    _categoryData.value = CategoryState(data = tempList1.distinctBy { it.categoryName }.toList())
////                    Log.d("EXpViewModel","_categoryData.value="+_categoryData.value.toString())
//                }
//            }
//        }.launchIn(viewModelScope)
    }


}