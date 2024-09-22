package com.example.classtrack.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@InstallIn(SingletonComponent::class)
@Module
object AppModule{
    @Singleton
    @Provides
    fun provideTeaClassRepository(): TeaClassesRepository{
        return TeaClassesRepository()
    }

    @Singleton
    @Provides
    fun provideStuClassRepository(): StuClassesRepository{
        return StuClassesRepository()
    }
}