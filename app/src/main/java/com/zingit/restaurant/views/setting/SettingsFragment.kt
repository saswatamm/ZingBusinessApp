package com.zingit.restaurant.views.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.BottomSheetDeactivateBinding
import com.zingit.restaurant.databinding.BottomSheetLogoutBinding
import com.zingit.restaurant.databinding.BottomSheetPrinterBinding
import com.zingit.restaurant.databinding.FragmentSettingsBinding



class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.apply {
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