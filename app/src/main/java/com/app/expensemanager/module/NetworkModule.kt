package com.app.expensemanager.module

import android.content.Context
import com.app.expensemanager.data.ExpensePreferences
import com.app.expensemanager.data.network.ExpenseService
import com.app.expensemanager.data.network.NetworkConstants
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providePreference(@ApplicationContext context: Context): ExpensePreferences {
        return ExpensePreferences(context)
    }

    @Singleton
    @Provides
    fun provideHttpClient(preferences: ExpensePreferences): OkHttpClient {

        val httpClient = OkHttpClient.Builder()

        var token: String? = null
        httpClient.addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
            token = runBlocking {
                preferences.accessToken.first()
            }
            if (!token.isNullOrBlank()) {
                request.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(request.build())
        })

        val logging = HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)

        return httpClient.build()

    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .setLenient()
            .create();
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideExpenseService(retrofit: Retrofit): ExpenseService =
        retrofit.create(ExpenseService::class.java)
}