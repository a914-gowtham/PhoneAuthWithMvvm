package com.gowtham.firebasephoneauthwithmvvm.fragments.login

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.gowtham.firebasephoneauthwithmvvm.R
import com.gowtham.firebasephoneauthwithmvvm.SharedViewModel
import com.gowtham.firebasephoneauthwithmvvm.databinding.FLoginBinding
import com.gowtham.firebasephoneauthwithmvvm.fragments.verify.AuthViewModel
import com.gowtham.firebasephoneauthwithmvvm.models.Country
import com.gowtham.firebasephoneauthwithmvvm.utils.*
import com.gowtham.firebasephoneauthwithmvvm.utils.Utils.toast
import com.gowtham.firebasephoneauthwithmvvm.utils.Utils.toastLong
import com.gowtham.firebasephoneauthwithmvvm.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class FLogin : Fragment() {

    private var country: Country? = null

    private lateinit var binding: FLoginBinding

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private var progressView: CustomProgressView?=null

    private val viewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FLoginBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressView = CustomProgressView(requireContext())
        setDataInView()
        subscribeObservers()
    }

    private fun setDataInView() {
        binding.viewmodel = viewModel
        setDefaultCountry()
        binding.txtCountryCode.setOnClickListener {
            findNavController().navigate(R.id.action_FLogin_to_FCountries)
        }
        binding.btnGetOtp.setOnClickListener {
            validate()
        }
    }

    private fun validate() {
        try {
            val mobileNo = viewModel.mobile.value?.trim()
            val country = viewModel.country.value
            when {
                mobileNo.isNullOrEmpty() -> toast(requireActivity(), "Enter mobile number")
                country == null -> toast(requireActivity(), "Select a country")
                Utils.isInvalidNo(country.code, mobileNo) -> toast(
                    requireActivity(),
                    "Enter valid mobile number"
                )
                Utils.isNoInternet(requireContext()) -> toast(requireActivity(),"No Internet Connection!")
                else -> {
                    viewModel.sendOtp()
                    viewModel.setProgress(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultCountry() {
        try {
            country = Utils.getDefaultCountry()
            val manager =
                requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)?
            manager?.let {
                val countryCode = manager.networkCountryIso ?: ""
                if (countryCode.isEmpty())
                    return
                val countries = Countries.getCountries()
                for (i in countries) {
                    if (i.code.equals(countryCode, true))
                        country = i
                }
                viewModel.setCountry(country!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun subscribeObservers() {
        try {
            sharedViewModel.country.observe(viewLifecycleOwner, {
                viewModel.setCountry(it)
            })

            viewModel.getProgress().observe(viewLifecycleOwner, {
                progressView?.toggle(it)
            })

            viewModel.getVerificationId().observe(viewLifecycleOwner, { vCode ->
                vCode?.let {
                    viewModel.setProgress(false)
                    viewModel.resetTimer()
                    viewModel.setVCodeNull()
                    viewModel.setEmptyText()
                    if (findNavController().isValidDestination(R.id.FLogin))
                    findNavController().navigate(R.id.action_FLogin_to_FVerify)
                }
            })

            viewModel.getFailed().observe(viewLifecycleOwner, {
                progressView?.dismiss()
            })

            viewModel.getTaskResult().observe(viewLifecycleOwner, { taskId ->
                if (taskId!=null && viewModel.getCredential().value?.smsCode.isNullOrEmpty())
                    viewModel.fetchUser(taskId)
            })

            viewModel.userProfileGot.observe(viewLifecycleOwner, { userId ->
                if (!userId.isNullOrEmpty() && viewModel.getCredential().value?.smsCode.isNullOrEmpty()
                               && findNavController().isValidDestination(R.id.FLogin)) {
                    toastLong(requireContext(),"Authenticated successfully using Instant verification")
                    val action=FLoginDirections.actionFLoginToFHome(userId,viewModel.lastRequestedMobile)
                    findNavController().navigate(action)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        try {
            progressView?.dismissIfShowing()
            super.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}