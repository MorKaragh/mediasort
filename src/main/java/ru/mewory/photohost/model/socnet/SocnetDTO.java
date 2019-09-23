package ru.mewory.photohost.model.socnet;

import java.util.Date;


public class SocnetDTO {
    private String author;
    private String text;
    private SocNet socnet;
    private Long id;
    private Integer userId;
    private Date date;
    private String link;

    public SocnetDTO(String author, String text) {
        this.author = author;
        this.text = text;
    }
    public SocnetDTO(Integer userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public SocnetDTO setLink(String link) {
        this.link = link;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public SocnetDTO setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getText() {
        return text;
    }

    public SocnetDTO setText(String text) {
        this.text = text;
        return this;
    }

    public SocNet getSocnet() {
        return socnet;
    }

    public SocnetDTO setSocnet(SocNet socnet) {
        this.socnet = socnet;
        return this;
    }

    public Long getId() {
        return id;
    }

    public SocnetDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public SocnetDTO setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public SocnetDTO setDate(Date date) {
        this.date = date;
        return this;
    }

    public static SocnetDTO fromInstagramLoaderObject(InstagramLoaderObject instagramLoaderObject) {
        return new SocnetDTO(instagramLoaderObject.getOwner().getUsername(), instagramLoaderObject.getText())
                .setDate(new Date(instagramLoaderObject.getCreated_at() * 1000))
                .setSocnet(SocNet.INSTAGRAM);
    }

}
