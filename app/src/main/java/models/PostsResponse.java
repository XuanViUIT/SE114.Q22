package models;

import java.util.List;

public class PostsResponse {
    private String status;
    private Integer count;
    private List<Post> data;
    public String getStatus() {
        return status;
    }
    public Integer getCount() {
        return count;
    }
    public List<Post> getData() {
        return data;
    }
}
