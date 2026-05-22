package models;

import com.google.gson.annotations.SerializedName;
import models.User;
public class RegisterResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    public String getMessage() {
        return message;
    }
    public String getStatus() {
        return status;
    }
    public User getUser() {
        return user;
    }
}
