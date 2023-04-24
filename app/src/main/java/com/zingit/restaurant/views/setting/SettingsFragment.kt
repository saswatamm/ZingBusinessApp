package com.zingit.restaurant.views.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zingit.restaurant.MainActivity
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.BottomSheetDeactivateBinding
import com.zingit.restaurant.databinding.BottomSheetLogoutBinding
import com.zingit.restaurant.databinding.BottomSheetPrinterBinding
import com.zingit.restaurant.databinding.FragmentSettingsBinding
import com.zingit.restaurant.viewModel.RestaurantProfileViewModel
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    private  val TAG = "SettingsFragment"
    private val viewModelSignUp: SignUpLoginViewModel by viewModels()
    private val restaurantProfileViewModel: RestaurantProfileViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        restaurantProfileViewModel.getUserData()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = restaurantProfileViewModel
            viewLifecycleOwner.lifecycle.coroutineScope.launchWhenCreated {
                restaurantProfileViewModel.restaurantProfileData.collect{
                Glide.with(this@SettingsFragment)
                    .load(it.data?.outletImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(profileImage)
                }


            }


            help.setOnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_helpFragment)
            }
            testPrinter.setOnClickListener {
                testPrinter()
            }
            logout.setOnClickListener {
                logout()
            }
            requestDeactivate.setOnClickListener {
                deactivateAccount()
            }
        }

        return binding.root
    }

    fun testPrinter() {
        val binding: BottomSheetPrinterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_sheet_printer,
            null,
            false
        )
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialog.setCancelable(false)
        binding.apply {
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            close.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(binding.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }

    fun logout() {
        val binding: BottomSheetLogoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_sheet_logout,
            null,
            false
        )
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialog.setCancelable(false)
        binding.apply {
            logout.setOnClickListener {
                viewModelSignUp.signOut()
                dialog.dismiss()
                startActivity(Intent(requireActivity(), MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                requireActivity().finishAffinity()
            }
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            close.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(binding.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }

    fun deactivateAccount() {
        val binding: BottomSheetDeactivateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_sheet_deactivate,
            null,
            false
        )
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialog.setCancelable(false)
        binding.apply {
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            close.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(binding.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }


}