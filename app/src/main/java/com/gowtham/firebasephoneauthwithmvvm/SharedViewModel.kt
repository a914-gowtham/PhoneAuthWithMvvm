package com.gowtham.firebasephoneauthwithmvvm

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gowtham.firebasephoneauthwithmvvm.models.Country
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@ActivityScoped
@Singleton
class SharedViewModel @ViewModelInject
constructor(@ApplicationContext private val context: Context) : ViewModel() {

    val country = MutableLiveData<Country>()

    fun setCountry(country: Country) {
        this.country.value = country
    }

    override fun onCleared() {
        super.onCleared()
    }
}