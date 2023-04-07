package com.zingit.restaurant.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class RestaurantProfileViewModel @Inject
constructor(
    private var firebaseRepository: FirebaseRepository,
):ViewModel(){

    private val _restaurantProfileData = MutableStateFlow(RestaurantProfileState())
    val restaurantProfileData: StateFlow<RestaurantProfileState> = _restaurantProfileData





    fun getUserData() {
        firebaseRepository.getRestaurantProfileDate().onEach {
            when (it) {
                is Resource.Loading -> {
                    _restaurantProfileData.value = RestaurantProfileState(isLoading = true)
                }
                is Resource.Error -> {
                    _restaurantProfileData.value = RestaurantProfileState(error = it.message ?: "")
                }
                is Resource.Success -> {
                    _restaurantProfileData.value = RestaurantProfileState(data = it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

}