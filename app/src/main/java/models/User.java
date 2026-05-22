package models;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("address")
    private String address;
    @SerializedName("description")
    private String description;
    @SerializedName("phone")
    private String phone;
    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getAvatarUrl() { return avatarUrl; }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public String getAddress() { return address; }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getPhone() { return phone; }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
