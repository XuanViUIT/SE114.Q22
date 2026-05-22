package models;

public class UpdateProfileResponse {
    private String status;
    private String message;
    private User user;
    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public User getUser() {
        return user;
    }
}
