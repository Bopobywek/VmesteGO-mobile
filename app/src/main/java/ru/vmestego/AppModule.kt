package ru.vmestego

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesTicketsRepository(@ApplicationContext appContext: Context) : TicketsRepository =
        TicketsRepositoryImpl(AppDatabase.getDatabase(appContext).ticketDao())
}