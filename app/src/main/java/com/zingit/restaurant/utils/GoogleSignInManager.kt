package com.zingit.restaurant.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GoogleSignInManager @Inject constructor(@ApplicationContext private val context :Context,private val googleSignInOptions: GoogleSignInOptions){

    fun getClient(): GoogleSignInClient {
        return GoogleSignIn.getClient(context, googleSignInOptions)
    }

    fun getAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun signIn(activity: Activity, requestCode: Int) {
        val signInIntent = getClient().signInIntent
        activity.startActivityForResult(signInIntent, requestCode)
    }
    fun signOut(): Task<Void> {
        return getClient().signOut()
    }
}