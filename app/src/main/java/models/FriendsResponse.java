package models;

import java.util.List;

public class FriendsResponse {
    private String status;
    private String message;
    private Integer user_id;
    private Integer count;
    private List<User> friends;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Integer getCount() {
        return count;
    }

    public List<User> getFriends() {
        return friends;
    }
}
