package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.util.Result;
import retrofit2.Response;

/**
 * API Service wrapper that provides async operations with proper error handling
 * Uses ExecutorService for background operations (2025 best practice for Java)
 */
public class ApiService {
    private static final String TAG = "ApiService";
    private final ApiRequestData apiRequestData;
    private final ExecutorService executorService;

    public ApiService(ApiRequestData apiRequestData) {
        this.apiRequestData = apiRequestData;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Fetches users from the API asynchronously
     * Wraps the result in a Result type for proper error handling
     */
    public Future<Result<List<User>>> getUsersAsync() {
        return executorService.submit(() -> {
            try {
                Response<List<User>> response = apiRequestData.getUsers().execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    if (users.isEmpty()) {
                        return new Result.Error<>("No users found");
                    }
                    return new Result.Success<>(users);
                } else {
                    String errorMsg = "HTTP " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            if (errorBody != null && !errorBody.isEmpty()) {
                                errorMsg += ": " + errorBody;
                            }
                        } catch (IOException e) {
                            Log.w(TAG, "Failed to read error body", e);
                        }
                    }
                    return new Result.Error<>(errorMsg);
                }
            } catch (IOException e) {
                Log.e(TAG, "Network error fetching users", e);
                return new Result.Error<>(e);
            } catch (Exception e) {
                Log.e(TAG, "Error fetching users", e);
                return new Result.Error<>(e);
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

