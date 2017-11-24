package ru.mewory.photohost.model.report;

/**
 * Created by tookuk on 11/23/17.
 */
public class ReportThemeCounter {
    private int vkPosts;
    private int instagramPosts;
    private String theme;

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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
