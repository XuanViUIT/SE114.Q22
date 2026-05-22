package models;

public class CreatePost {
    private Integer user_id;
    private String content;

    public CreatePost(Integer user_id, String content) {
        this.user_id = user_id;
        this.content = content;
    }

    public Integer getUser_id() {
        return user_id;
    }
    public String getContent() {
        return content;
    }
}
