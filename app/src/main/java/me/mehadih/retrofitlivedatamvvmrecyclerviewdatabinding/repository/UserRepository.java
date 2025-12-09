package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api.ApiService;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.util.Result;

/**
 * Repository pattern implementation with proper error handling
 * Uses dependency injection and Result wrapper for type-safe error handling (2025 best practice)
 */
@Singleton
public class UserRepository {
    private static final String TAG = "UserRepository";
    private final ApiService apiService;
    private final MutableLiveData<Result<List<User>>> usersLiveData = new MutableLiveData<>();

    @Inject
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Fetches users from the API
     * Returns LiveData with Result wrapper for proper state management
     */
    public LiveData<Result<List<User>>> getUsers() {
        // Set loading state (use postValue for thread safety)
        usersLiveData.postValue(new Result.Loading<>());

        // Fetch data asynchronously
        Future<Result<List<User>>> future = apiService.getUsersAsync();
        
        // Handle result on background thread
        new Thread(() -> {
            try {
                Result<List<User>> result = future.get();
                usersLiveData.postValue(result);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Error getting users", e);
                usersLiveData.postValue(new Result.Error<>(e));
            }
        }).start();

        return usersLiveData;
    }

    /**
     * Refreshes the user list
     */
    public void refreshUsers() {
        getUsers();
    }
}
