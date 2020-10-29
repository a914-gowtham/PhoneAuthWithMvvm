package com.gowtham.firebasephoneauthwithmvvm.utils

import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.gowtham.firebasephoneauthwithmvvm.models.Country
import com.gowtham.firebasephoneauthwithmvvm.views.CustomProgressView

object Utils {

    @BindingAdapter("main", "secondText")
    @JvmStatic
    fun setBoldString(view: TextView, maintext: String, sequence: String) {
        view.text = getBoldText(maintext, sequence)
    }

    @JvmStatic
    fun getBoldText(text: String, name: String): SpannableStringBuilder {
        val str = SpannableStringBuilder(text)
        val textPosition = text.indexOf(name)
        str.setSpan(
            android.text.style.StyleSpan(Typeface.BOLD),
            textPosition, textPosition + name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return str
    }

    fun getDefaultCountry() = Country("IN", "India", "+91", "INR")

    fun toast(context: Context,msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun toastLong(context: Context,msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun isInvalidNo(code: String, mobileNo: String): Boolean {
        try {
            val phoneUtil = PhoneNumberUtil.getInstance()
            val phNumberProto: Phonenumber.PhoneNumber = phoneUtil.parse(
                mobileNo, code
            )
            return !phoneUtil.isValidNumber(phNumberProto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun isNetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            (capabilities != null &&
                    ((capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)))
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            (activeNetworkInfo != null && activeNetworkInfo.isConnected)
        }
    }

    fun isNoInternet(context: Context) = !isNetConnected(context)
}

fun NavController.isValidDestination(destination: Int): Boolean {
    return destination == this.currentDestination!!.id
}

fun Char.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this.toString())

fun CustomProgressView.toggle(show: Boolean){
    if (show)
        this.show()
    else
        this.dismiss();
}

fun CustomProgressView.dismissIfShowing(){
    if (this.isShowing)
        this.dismiss()
}

