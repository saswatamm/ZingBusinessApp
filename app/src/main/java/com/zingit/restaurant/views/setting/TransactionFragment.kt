package com.zingit.restaurant.views.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.zingit.restaurant.R
import com.zingit.restaurant.adapter.ViewPagerAdapter
import com.zingit.restaurant.databinding.FragmentTransactionBinding
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import com.zingit.restaurant.viewModel.TransactionViewModel
import com.zingit.restaurant.views.order.ActiveFragment
import com.zingit.restaurant.views.order.HistoryFragment

class TransactionFragment : Fragment() {

    lateinit var binding:FragmentTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var fragments = mutableListOf<Fragment>()
        fragments.add(QrVolumeFragment())
        fragments.add(LinkVolumeFragment())
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, fragments)
        binding.viewpager.adapter = adapter
        val titles = mutableListOf("QR Volume", "Link Volume")
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        binding.apply {
            backArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}