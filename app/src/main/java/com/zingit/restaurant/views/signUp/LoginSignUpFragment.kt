package com.zingit.restaurant.views.signUp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentLoginSignUpBinding
import com.zingit.restaurant.utils.GoogleSignInManager
import com.zingit.restaurant.utils.hideKeyboard
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginSignUpFragment : Fragment() {

    lateinit var binding:FragmentLoginSignUpBinding
    private val viewModel: SignUpLoginViewModel by viewModels()
    private val RC_SIGN_IN = 123
    @Inject lateinit var googleSignInManager: GoogleSignInManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_sign_up, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        view?.hideKeyboard()
        viewModel.error.observe(viewLifecycleOwner){
            val snack = Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
            snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red))
            snack.show()
        }
        binding.apply {
            googleLogin.setOnClickListener {
                googleSignInManager.signIn(requireActivity(),RC_SIGN_IN)
            }
        }



        return binding.root
    }




}