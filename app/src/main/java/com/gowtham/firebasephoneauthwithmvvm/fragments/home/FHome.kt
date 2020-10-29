package com.gowtham.firebasephoneauthwithmvvm.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.gowtham.firebasephoneauthwithmvvm.databinding.FHomeBinding

class FHome : Fragment() {

    private lateinit var binding: FHomeBinding

    val args by navArgs<FHomeArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FHomeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userId=args.userId
        binding.mobile=args.mobile


    }
}