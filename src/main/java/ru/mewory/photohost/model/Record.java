package ru.mewory.photohost.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tookuk on 10/1/17.
 */
@Entity
@Table(name="records")
public class Record {

    private Long id;

    @JsonProperty("location")
    private String location;

    @JsonProperty("theme")
    private String theme;

    @JsonProperty("author")
    private String author;

    @JsonProperty("tags")
    @Transient
    private ArrayList<String> tags;

    @Transient
    private ArrayList<Tag> tagsObjects;

    @JsonProperty("description")
    private String description;

    private Date date;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Transient
    public ArrayList<Tag> getTagsObjects() {
        if (tagsObjects == null){
            tagsObjects = new ArrayList<>();
        }
        return tagsObjects;
    }

    public void setTagsObjects(ArrayList<Tag> tagsObjects) {
        this.tagsObjects = tagsObjects;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public ArrayList<String> getTags() {
        if (tags == null){
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
