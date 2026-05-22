package models;

public class PostResponse {
    private String status;
    private String message;
    private Post data;
    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public Post getData() {
        return data;
    }
}
