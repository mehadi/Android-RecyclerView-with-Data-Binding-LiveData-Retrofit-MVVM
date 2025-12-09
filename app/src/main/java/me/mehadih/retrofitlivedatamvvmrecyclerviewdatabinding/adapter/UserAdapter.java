package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.R;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.databinding.ItemUserBinding;
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;

/**
 * Created By - Mehadi
 * Created On - 2/6/2020 : 1:23 PM
 * Email - hi@mehadih.me
 * Website - www.mehadih.me
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> users;
    private OnItemClickListener listener;
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemUserBinding itemUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user, parent, false);
        return new UserViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (users != null && position >= 0 && position < users.size()) {
            User currentUser = users.get(position);
            holder.itemUserBinding.setUser(currentUser);
        }
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        } else {
            return 0;
        }
    }

    /**
     * Updates the user list using DiffUtil for efficient updates (2025 best practice)
     * Only animates and updates changed items instead of refreshing the entire list
     */
    public void setUserList(ArrayList<User> newUsers) {
        if (users == null) {
            users = new ArrayList<>();
            if (newUsers != null) {
                users.addAll(newUsers);
            }
            notifyItemRangeInserted(0, users.size());
        } else {
            UserDiffCallback diffCallback = new UserDiffCallback(users, newUsers);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            users.clear();
            if (newUsers != null) {
                users.addAll(newUsers);
            }
            diffResult.dispatchUpdatesTo(this);
        }
    }
    public User getCurrentItemAt(int position) {
        if (users != null && position >= 0 && position < users.size()) {
            return users.get(position);
        }
        return null;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private ItemUserBinding itemUserBinding;

        public UserViewHolder(@NonNull ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            this.itemUserBinding = itemUserBinding;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getCurrentItemAt(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
