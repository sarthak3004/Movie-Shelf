package com.sarthak.movieshelf.di

import com.sarthak.movieshelf.service.AuthService
import com.sarthak.movieshelf.service.FireStoreService
import com.sarthak.movieshelf.service.impl.AuthServiceImpl
import com.sarthak.movieshelf.service.impl.FireStoreServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAuthService(impl: AuthServiceImpl): AuthService
    @Binds abstract fun provideStorageService(impl: FireStoreServiceImpl): FireStoreService

}