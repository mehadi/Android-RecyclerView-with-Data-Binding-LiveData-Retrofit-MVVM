package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api.ApiRequestData;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.api.RetroServer;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created By - Mehadi
 * Created On - 2/6/2020 : 1:14 PM
 * Email - hi@mehadih.me
 * Website - www.mehadih.me
 */
public class UserRepository {

    private ArrayList<User> userArrayList = new ArrayList<>();
    private MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();
    private Application application;

    public UserRepository(Application application) {
        this.application = application;
    }


    public MutableLiveData<List<User>> getUsers() {
        ApiRequestData apiService = RetroServer.getRetrofitServer();
        Call<List<User>> call = apiService.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    userArrayList = (ArrayList<User>) response.body();
                    mutableLiveData.setValue(userArrayList);
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });
        return mutableLiveData;
    }

}
