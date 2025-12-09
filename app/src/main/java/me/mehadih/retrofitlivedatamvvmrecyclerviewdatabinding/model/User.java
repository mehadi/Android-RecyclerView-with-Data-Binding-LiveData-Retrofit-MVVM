package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * User model class representing a user entity from the API
 * 
 * This class follows 2025 best practices:
 * - Uses Gson annotations for JSON serialization/deserialization
 * - Implements proper null safety with annotations
 * - Provides getters and setters for data binding compatibility
 * 
 * @author Mehadi
 * @since 2025
 */
public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    @Nullable
    private String name;

    @SerializedName("username")
    @Nullable
    private String username;

    @SerializedName("email")
    @Nullable
    private String email;

    /**
     * Default constructor required for Gson
     */
    public User() {
    }

    /**
     * Constructor with all fields
     */
    public User(int id, @Nullable String name, @Nullable String username, @Nullable String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    /**
     * Returns a display name for the user
     * Falls back to username if name is null
     */
    @NonNull
    public String getDisplayName() {
        return name != null && !name.isEmpty() ? name : (username != null ? username : "Unknown User");
    }

    @Override
    @NonNull
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
