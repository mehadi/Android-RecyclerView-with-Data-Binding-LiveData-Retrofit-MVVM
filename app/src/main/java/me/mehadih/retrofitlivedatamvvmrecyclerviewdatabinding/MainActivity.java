package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.adapter.UserAdapter;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.databinding.ActivityMainBinding;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {


    private UserViewModel userViewModel;
    UserAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        adapter.setOnItemClickListener(user -> {
            Toast.makeText(getApplicationContext(),"You clicked : "+user.getName(), Toast.LENGTH_SHORT).show();
        });

        getData();

    }

    private void getData() {
        userViewModel.getAllUsers().observe(this, userList -> {
            adapter.setUserList((ArrayList<User>) userList);
        });
    }


}
