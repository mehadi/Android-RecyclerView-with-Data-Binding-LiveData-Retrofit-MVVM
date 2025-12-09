package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model.User;

/**
 * DiffUtil callback for efficient RecyclerView updates (2025 best practice)
 * Only updates changed items instead of refreshing the entire list
 */
public class UserDiffCallback extends DiffUtil.Callback {
    private final List<User> oldList;
    private final List<User> newList;

    public UserDiffCallback(List<User> oldList, List<User> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        User oldUser = oldList.get(oldItemPosition);
        User newUser = newList.get(newItemPosition);
        return oldUser != null && newUser != null && oldUser.getId() == newUser.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        User oldUser = oldList.get(oldItemPosition);
        User newUser = newList.get(newItemPosition);
        return oldUser != null && newUser != null
                && oldUser.getId() == newUser.getId()
                && equals(oldUser.getName(), newUser.getName())
                && equals(oldUser.getEmail(), newUser.getEmail())
                && equals(oldUser.getUsername(), newUser.getUsername());
    }

    private boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}

