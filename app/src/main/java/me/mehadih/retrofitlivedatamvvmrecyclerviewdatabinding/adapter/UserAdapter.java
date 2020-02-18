package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
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
        User currentUser = users.get(position);
        holder.itemUserBinding.setUser(currentUser);
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        } else {
            return 0;
        }
    }

    public void setUserList(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
    public User getCurrentItemAt(int position) {
        return users.get(position);
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
