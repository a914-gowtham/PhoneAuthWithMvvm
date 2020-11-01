package com.gowtham.firebasephoneauthwithmvvm.fragments.verify

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.gowtham.firebasephoneauthwithmvvm.MainActivity
import com.gowtham.firebasephoneauthwithmvvm.models.Country
import com.gowtham.firebasephoneauthwithmvvm.utils.LogInFailedState
import com.gowtham.firebasephoneauthwithmvvm.utils.Utils.toast
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthRepo @Inject constructor(
    @ActivityRetainedScoped val actContxt: MainActivity,
    @ApplicationContext val context: Context) {

    private val verificationId: MutableLiveData<String> = MutableLiveData()

    val credential: MutableLiveData<PhoneAuthCredential> = MutableLiveData()

    private val taskResult: MutableLiveData<Task<AuthResult>> = MutableLiveData()

    private val failedState: MutableLiveData<LogInFailedState> = MutableLiveData()

    private val auth = FirebaseAuth.getInstance()

    fun sendOtp(activity: Activity,country: Country, mobile: String) {
        val number = country.noCode + " " + mobile
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(actContxt)
            .setCallbacks(listener)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    taskResult.value = task
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                        toast(context, "Invalid verification code!")
                    failedState.value = LogInFailedState.SignIn
                }
            }
    }

    fun setCredential(credential: PhoneAuthCredential) {
        signInWithPhoneAuthCredential(credential)
    }

    fun getVCode(): MutableLiveData<String> {
        return verificationId
    }

    fun setVCodeNull() {
        verificationId.value = null
    }

    fun clearOldAuth(){
        credential.value=null
        taskResult.value=null
    }

    fun getCredential(): LiveData<PhoneAuthCredential> {
        return credential
    }

    fun getTaskResult(): LiveData<Task<AuthResult>> {
        return taskResult
    }

    fun getFailed(): LiveData<LogInFailedState> {
        return failedState
    }

    private val listener=object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted:$credential")
            this@AuthRepo.credential.value = credential
            Handler().postDelayed({
                signInWithPhoneAuthCredential(credential)
            }, 1000)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            failedState.value = LogInFailedState.Verification
            Log.e("TAG", "onVerificationFailed: ${e.message}")
            when (e) {
                is FirebaseAuthInvalidCredentialsException ->
                    toast(context, "Invalid Request")
                else -> toast(context, e.message.toString())
            }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)
            Log.d("TAG", "onCodeSent:$verificationId")
            this@AuthRepo.verificationId.value = verificationId
            toast(context, "Verification code sent successfully")
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
        }
    }
}