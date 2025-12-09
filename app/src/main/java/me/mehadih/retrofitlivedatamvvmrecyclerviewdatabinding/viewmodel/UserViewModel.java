package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.repository.UserRepository;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.util.Result;

/**
 * ViewModel with dependency injection and proper state management
 * Uses Result wrapper for type-safe error handling (2025 best practice)
 */
@HiltViewModel
public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    @Inject
    public UserViewModel(@NonNull Application application, UserRepository repository) {
        super(application);
        this.repository = repository;
        loadUsers();
    }

    /**
     * Loads users from repository and maps Result to separate LiveData streams
     * Observes repository LiveData and transforms Result into separate streams
     */
    private void loadUsers() {
        // Observe repository LiveData (ViewModel constructor runs on main thread)
        repository.getUsers().observeForever(result -> {
            if (result instanceof Result.Loading) {
                isLoadingLiveData.setValue(true);
                errorMessageLiveData.setValue(null);
            } else if (result instanceof Result.Success) {
                isLoadingLiveData.setValue(false);
                List<User> users = ((Result.Success<List<User>>) result).getData();
                usersLiveData.setValue(users != null ? users : new ArrayList<>());
                errorMessageLiveData.setValue(null);
            } else if (result instanceof Result.Error) {
                isLoadingLiveData.setValue(false);
                String errorMsg = ((Result.Error<List<User>>) result).getMessage();
                errorMessageLiveData.setValue(errorMsg != null ? errorMsg : "An error occurred");
                usersLiveData.setValue(new ArrayList<>());
            }
        });
    }

    /**
     * Returns the list of users
     */
    public LiveData<List<User>> getAllUsers() {
        return usersLiveData;
    }

    /**
     * Returns error messages
     */
    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    /**
     * Returns loading state
     */
    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }

    /**
     * Refreshes the user list
     */
    public void refreshUsers() {
        repository.refreshUsers();
    }
}
