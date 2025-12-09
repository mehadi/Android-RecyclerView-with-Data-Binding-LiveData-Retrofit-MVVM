package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.adapter.UserAdapter;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.databinding.ActivityMainBinding;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.viewmodel.UserViewModel;

/**
 * Main Activity with modern Android architecture (2025 best practices)
 * Features:
 * - Hilt dependency injection
 * - MVVM architecture with LiveData
 * - SwipeRefreshLayout for pull-to-refresh
 * - Empty state and error state handling
 * - Material Design 3 components
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private UserAdapter adapter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        setupViewModel();
        observeViewModel();
    }

    /**
     * Sets up the toolbar
     */
    private void setupToolbar() {
        if (binding.toolbar != null) {
            setSupportActionBar(binding.toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(user -> {
            if (user != null && user.getName() != null) {
                Toast.makeText(
                    this,
                    getString(R.string.app_name) + ": " + user.getName(),
                    Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    /**
     * Sets up SwipeRefreshLayout for pull-to-refresh functionality
     */
    private void setupSwipeRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        
        // Use Material Design colors from theme
        int[] colors = new int[]{
            ContextCompat.getColor(this, android.R.color.holo_blue_bright),
            ContextCompat.getColor(this, android.R.color.holo_green_light),
            ContextCompat.getColor(this, android.R.color.holo_orange_light),
            ContextCompat.getColor(this, android.R.color.holo_red_light)
        };
        swipeRefreshLayout.setColorSchemeColors(colors);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userViewModel != null) {
                userViewModel.refreshUsers();
            }
        });
    }

    /**
     * Sets up ViewModel using ViewModelProvider (2025 best practice)
     */
    private void setupViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    /**
     * Observes ViewModel LiveData for users, errors, and loading states
     * Implements proper state management with empty state and error handling
     */
    private void observeViewModel() {
        // Observe users list
        userViewModel.getAllUsers().observe(this, userList -> {
            if (userList != null) {
                updateUserList(userList);
            }
        });

        // Observe error messages
        userViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showErrorState(errorMessage);
            }
        });

        // Observe loading state
        userViewModel.isLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                updateLoadingState(isLoading);
            }
        });
    }

    /**
     * Updates the user list and handles empty state
     */
    private void updateUserList(List<User> userList) {
        binding.swipeRefreshLayout.setRefreshing(false);
        
        if (userList.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            hideErrorState();
            adapter.setUserList(new ArrayList<>(userList));
        }
    }

    /**
     * Updates the loading state UI
     */
    private void updateLoadingState(boolean isLoading) {
        if (isLoading) {
            binding.progressIndicator.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            hideEmptyState();
            hideErrorState();
        } else {
            binding.progressIndicator.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows empty state when no users are available
     */
    private void showEmptyState() {
        binding.emptyStateLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.errorStateLayout.setVisibility(View.GONE);
    }

    /**
     * Hides empty state
     */
    private void hideEmptyState() {
        binding.emptyStateLayout.setVisibility(View.GONE);
    }

    /**
     * Shows error state with retry button
     */
    private void showErrorState(String errorMessage) {
        binding.swipeRefreshLayout.setRefreshing(false);
        binding.errorStateLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.GONE);
        
        binding.errorMessageText.setText(
            errorMessage != null ? errorMessage : getString(R.string.error_loading_users)
        );

        // Setup retry button
        binding.retryButton.setOnClickListener(v -> {
            if (userViewModel != null) {
                userViewModel.refreshUsers();
            }
        });
    }

    /**
     * Hides error state
     */
    private void hideErrorState() {
        binding.errorStateLayout.setVisibility(View.GONE);
    }
}
