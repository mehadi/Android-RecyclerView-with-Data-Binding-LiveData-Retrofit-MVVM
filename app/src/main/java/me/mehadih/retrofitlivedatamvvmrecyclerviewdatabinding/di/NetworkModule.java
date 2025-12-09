package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.di;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.BuildConfig;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api.ApiRequestData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Network module for dependency injection
 * Provides Retrofit, OkHttp, and API service instances
 */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(
            BuildConfig.DEBUG 
                ? HttpLoggingInterceptor.Level.BODY 
                : HttpLoggingInterceptor.Level.NONE
        );

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public ApiRequestData provideApiRequestData(Retrofit retrofit) {
        return retrofit.create(ApiRequestData.class);
    }

    @Provides
    @Singleton
    public me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api.ApiService provideApiService(
            ApiRequestData apiRequestData) {
        return new me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api.ApiService(apiRequestData);
    }
}

