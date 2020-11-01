package com.gowtham.firebasephoneauthwithmvvm.fragments.verify

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.PhoneAuthProvider
import com.gowtham.firebasephoneauthwithmvvm.R
import com.gowtham.firebasephoneauthwithmvvm.databinding.FVerifyBinding
import com.gowtham.firebasephoneauthwithmvvm.fragments.login.FLoginDirections
import com.gowtham.firebasephoneauthwithmvvm.utils.*
import com.gowtham.firebasephoneauthwithmvvm.utils.Utils.toast
import com.gowtham.firebasephoneauthwithmvvm.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FVerify : Fragment() {

    private lateinit var binding: FVerifyBinding

    private val viewModel by activityViewModels<AuthViewModel>()

    private lateinit var edtTexts: ArrayList<EditText>

    private var progressView: CustomProgressView?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FVerifyBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewmodel = viewModel
        progressView = CustomProgressView(requireContext())
        setDataInView()
        subscribeObservers()
/*
        if (arguments != null) {
            val s = FVerifyArgs.fromBundle(requireArguments()).country
            s.name.printMeD()
        } else
            "argument is null".printMeD()*/
    }

    private fun setDataInView() {
        try {
            edtTexts = ArrayList()
            edtTexts.add(binding.edtOne)
            edtTexts.add(binding.edtTwo)
            edtTexts.add(binding.edtThree)
            edtTexts.add(binding.edtFour)
            edtTexts.add(binding.edtFive)
            edtTexts.add(binding.edtSix)
            addListener()
            if (viewModel.resendTxt.value.isNullOrEmpty())
                viewModel.startTimer()
            binding.btnVerify.setOnClickListener {
                validateOtp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateOtp() {
        try {
            val otp = getOtpValue()
            when {
                otp.length < 6 -> toast(requireActivity(), "Enter valid otp")
                Utils.isNoInternet(requireContext()) -> {
                    toast(requireActivity(), "No Internet Connection")
                }
                else -> {
                    Log.d("TAG", "VerificaitionCode: ${viewModel.verifyCode} ")
                    Log.d("TAG", "OTP:$otp ")
                    val credential = PhoneAuthProvider.getCredential(viewModel.verifyCode, otp)
                    viewModel.setCredential(credential)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getOtpValue(): String {
        try {
            var otp = ""
            for (edtTxt in edtTexts)
                otp += edtTxt.text.toString().trim()
            return otp
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun addListener() {
        try {
            for (editText in edtTexts) {
                editText.addTextChangedListener(OtpWatcher(editText))
                editText.setOnKeyListener { _, keyCode: Int, _ ->
                    if (keyCode == KeyEvent.KEYCODE_DEL)
                        onKeyListener()
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onKeyListener() {
        try {
            val edtView: EditText = edtTexts[viewModel.ediPosition]
            if (edtView.text.toString().trim().isEmpty() && viewModel.ediPosition > 0) {
                viewModel.ediPosition -= 1
                edtTexts[viewModel.ediPosition].requestFocus()
            } else edtView.requestFocus()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun subscribeObservers() {
        try {
            viewModel.getCredential().observe(viewLifecycleOwner, { credential ->
                credential?.let {
                    val otp = credential.smsCode
                    edtTexts.forEachIndexed { i, editText ->
                        editText.text = otp?.get(i)?.toEditable()
                    }
                    viewModel.setVProgress(true)
                }
            })

            viewModel.getVProgress().observe(viewLifecycleOwner, { show ->
                progressView?.toggle(show)
            })

            viewModel.getFailed().observe(viewLifecycleOwner, {
                viewModel.setVProgress(false)
                viewModel.setEmptyText()
                edtTexts[0].requestFocus()
            })

            viewModel.getVerificationId().observe(viewLifecycleOwner, { vCode ->
                vCode?.let {
                    viewModel.setVProgress(false)
                    viewModel.setVCodeNull()
                    viewModel.startTimer()
                }
            })

            viewModel.getTaskResult().observe(viewLifecycleOwner, { taskId ->
                taskId?.let {
                    viewModel.fetchUser(taskId)
                }
            })

            viewModel.userProfileGot.observe(viewLifecycleOwner, { userId ->
                if (!userId.isNullOrEmpty() && findNavController().isValidDestination(R.id.FVerify)) {
                    val action= FVerifyDirections.actionFVerifyToFHome(userId,viewModel.lastRequestedMobile)
                    findNavController().navigate(action)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class OtpWatcher(private val v: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when (v.id) {
                R.id.edt_one ->
                    changeFocus(text, 0, 1)
                R.id.edt_two ->
                    changeFocus(text, 0, 2)
                R.id.edt_three ->
                    changeFocus(text, 1, 3)
                R.id.edt_four ->
                    changeFocus(text, 2, 4)
                R.id.edt_five ->
                    changeFocus(text, 3, 5)
                R.id.edt_six ->
                    changeFocus(text, 4, 5)
                else -> {
                    if (text.isEmpty())
                        edtTexts[5].requestFocus()
                }
            }
        }
    }

    private fun changeFocus(text: String, previous1: Int, next: Int) {
        viewModel.ediPosition = next - 1
        edtTexts[if (text.isEmpty()) previous1 else next].requestFocus()
    }

    override fun onDestroy() {
        viewModel.clearAll()
        progressView?.dismissIfShowing()
        super.onDestroy()
    }
}