package ru.mewory.mediasort.model.report;


public class ReportElement {
    private int vkPosts;
    private int instagramPosts;
    private long count;
    private long vkCount;
    private long instagramCount;
    private String location;
    private String description;
    private String additionalText;
    private Long userCount;

    public String getAdditionalText() {
        return additionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
    }

    public long getVkCount() {
        return vkCount;
    }

    public void setVkCount(long vkCount) {
        this.vkCount = vkCount;
    }

    public long getInstagramCount() {
        return instagramCount;
    }

    public void setInstagramCount(long instagramCount) {
        this.instagramCount = instagramCount;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVkPosts() {
        return vkPosts;
    }

    public void setVkPosts(int vkPosts) {
        this.vkPosts = vkPosts;
    }

    public int getInstagramPosts() {
        return instagramPosts;
    }

    public void setInstagramPosts(int instagramPosts) {
        this.instagramPosts = instagramPosts;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String theme) {
        this.location = theme;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getUserCount() {
        return userCount;
    }
}
