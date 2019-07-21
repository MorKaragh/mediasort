package ru.mewory.photohost.model.socnet;


public class Owner {
    private String id;
    private boolean is_verified;
    private String profile_pic_url;
    private String username;


    // Getter Methods

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    // Setter Methods

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}