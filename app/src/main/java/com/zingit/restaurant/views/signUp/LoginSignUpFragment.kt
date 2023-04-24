package com.zingit.restaurant.views.signUp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zingit.restaurant.R
import com.zingit.restaurant.databinding.FragmentLoginSignUpBinding
import com.zingit.restaurant.utils.GoogleSignInManager
import com.zingit.restaurant.utils.Utils.hideKeyboard
import com.zingit.restaurant.viewModel.SignUpLoginViewModel
import com.zingit.restaurant.views.RootActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginSignUpFragment : Fragment() {

    lateinit var binding:FragmentLoginSignUpBinding
    private val viewModelSignUp: SignUpLoginViewModel by viewModels()
    private val RC_SIGN_IN = 123
    @Inject lateinit var googleSignInManager: GoogleSignInManager
    private  val TAG = "LoginSignUpFragment"



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
        binding.viewModel = viewModelSignUp
        view?.hideKeyboard()
        viewModelSignUp.error.observe(viewLifecycleOwner){
            val snack = Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
            snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red))
            snack.show()
        }
        binding.apply {
            viewModelSignUp.loadingLivedata.observe(viewLifecycleOwner){
                if (it)
                    loader.visibility = View.VISIBLE
                else
                    loader.visibility = View.GONE
            }
            viewModelSignUp.dataSignIn.observe(viewLifecycleOwner){
                if (it)
                startActivity(Intent(requireContext(),RootActivity::class.java))
                else
                    Log.e(TAG, "onCreateView: not working ", )
            }
        }
        
        googleSignInManager.getAccount()?.let {
            Log.e(TAG, "onCreateView: ${it}", )
          
        }







        return binding.root
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }


//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//            // Signed in successfully, show UI.
//           Log.e(TAG, "handleSignInResult: ${account?.id} ${account.displayName}", )
//            startActivity(Intent(requireContext(),HomeMainActivity::class.java))
//
//        } catch (e: ApiException) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
//
//        }
//    }







}