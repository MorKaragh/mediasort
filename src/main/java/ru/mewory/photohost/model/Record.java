package ru.mewory.photohost.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.CollectionUtils;

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

    @JsonProperty("tags")
    @Transient
    private ArrayList<String> tags;

    @Transient
    private ArrayList<Tag> tagsObjects;

    @JsonProperty("description")
    private String description;

    @JsonProperty("additionalText")
    @Column(length = 400)
    private String additionalText;

    @JsonProperty("commentId")
    private Long commentId;

    private Date date;

    public String getAdditionalText() {
        return additionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

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
        if (!CollectionUtils.isEmpty(tagsObjects)){
            tags.clear();
            for (Tag t:tagsObjects) {
                tags.add(t.getName());
            }
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
}
