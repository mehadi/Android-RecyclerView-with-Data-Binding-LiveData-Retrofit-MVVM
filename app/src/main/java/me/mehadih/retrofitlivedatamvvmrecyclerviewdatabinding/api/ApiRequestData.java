package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api;

import java.util.List;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * API interface for Retrofit
 * Using Call for now, can be converted to suspend functions when migrating to Kotlin
 */
public interface ApiRequestData {
    @GET("users")
    Call<List<User>> getUsers();
}
