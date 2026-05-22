package models;

public class LoginResponse {
    private String status;
    private String message;
    private User user;

    public String getStatus() { return status; }
    public User getUser() { return user; }

    public String getMessage() { return message; }

}
