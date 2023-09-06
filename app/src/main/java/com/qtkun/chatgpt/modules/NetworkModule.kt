package com.qtkun.chatgpt.modules

import android.content.Context
import androidx.room.Room
import com.qtkun.chatgpt.room.ChatGPTDao
import com.qtkun.chatgpt.contant.BASE_URL
import com.qtkun.chatgpt.net.ApiResultCallAdapterFactory
import com.qtkun.chatgpt.room.AppDataBase
import com.qtkun.chatgpt.service.ChatGPTService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(getRequestHeader())
//            .addInterceptor(commonInterceptor())
            .addInterceptor(getHttpLoggingInterceptor())
//            .addNetworkInterceptor(getCacheInterceptor())
//            .cache(getCache(context))
//            .cookieJar(cookieJar)
            .build()
    }

    @Singleton
    @ChatGPTRetrofit
    @Provides
    fun provideChatGPTRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun provideChatGPTService(@ChatGPTRetrofit retrofit: Retrofit): ChatGPTService {
        return retrofit.create(ChatGPTService::class.java)
    }
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "chat_gpt_room.db")
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideChatGPTDao(appDataBase: AppDataBase): ChatGPTDao = appDataBase.getChatGPTDao()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatGPTRetrofit