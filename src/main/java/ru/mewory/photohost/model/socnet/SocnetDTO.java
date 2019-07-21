package ru.mewory.photohost.model.socnet;

import java.util.Date;

/**
 * Created by tookuk on 11/13/17.
 */
public class SocnetDTO {
    private String author;
    private String text;
    private SocNet socnet;
    private Long id;
    private Integer userId;
    private Date date;

    public SocnetDTO(String author, String text) {
        this.author = author;
        this.text = text;
    }
    public SocnetDTO(Integer userId, String text) {
        this.userId = userId;
        this.text = text;
    }
    public SocNet getSocnet() {
        return socnet;
    }

    public void setSocnet(SocNet socnet) {
        this.socnet = socnet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String name) {
        this.author = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }


    public static SocnetDTO fromInstagramLoaderObject(InstagramLoaderObject instagramLoaderObject) {
        SocnetDTO dto = new SocnetDTO(instagramLoaderObject.getOwner().getUsername(), instagramLoaderObject.getText());
        dto.setDate(new Date(instagramLoaderObject.getCreated_at() * 1000));
        dto.setSocnet(SocNet.INSTAGRAM);
        return dto;
    }

}
