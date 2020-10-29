package com.gowtham.firebasephoneauthwithmvvm.di

import com.gowtham.firebasephoneauthwithmvvm.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun provideMainActivity(): MainActivity {
        return MainActivity()
    }

}