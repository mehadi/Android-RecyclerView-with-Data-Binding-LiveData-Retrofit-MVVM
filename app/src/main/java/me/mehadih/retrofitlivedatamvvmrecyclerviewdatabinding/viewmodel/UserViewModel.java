package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.repository.UserRepository;

/**
 * Created By - Mehadi
 * Created On - 2/6/2020 : 1:18 PM
 * Email - hi@mehadih.me
 * Website - www.mehadih.me
 */

public class UserViewModel extends AndroidViewModel {
    UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public LiveData<List<User>> getAllUsers() {
        return repository.getUsers();
    }

}
