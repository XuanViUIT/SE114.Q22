package models;

public class UpdateProfile {
    private String name;
    private String phone;
    private String address;
    private String description;
    private String avatar_url;

    public UpdateProfile(String name, String phone, String address, String description, String avatar_url) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.avatar_url = avatar_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
