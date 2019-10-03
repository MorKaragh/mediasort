package ru.mewory.mediasort.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;


@Entity
@Table(name="records",
        indexes = {@Index(columnList = "description"),
                @Index(columnList = "additionalText"),
                @Index(columnList = "location"),
                @Index(columnList = "theme"),
                @Index(columnList = "commentId")})
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
    @Column(length = 1000)
    private String description;

    @JsonProperty("additionalText")
    @Column(length = 1000)
    private String additionalText;

    @JsonProperty("commentId")
    private Long commentId;

    @JsonProperty("vedomstvo")
    @Transient
    private Boolean authorIsVedomstvo;

    private Date date;

    public void trimAll(){
        location = StringUtils.trim(location);
        theme = StringUtils.trim(theme);
        description = StringUtils.trim(description);
        additionalText = StringUtils.trim(additionalText);
    }

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

    public Boolean getAuthorIsVedomstvo() {
        return authorIsVedomstvo;
    }

    public void setAuthorIsVedomstvo(Boolean authorIsVedomstvo) {
        this.authorIsVedomstvo = authorIsVedomstvo;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", theme='" + theme + '\'' +
                ", tags=" + tags +
                ", tagsObjects=" + tagsObjects +
                ", description='" + description + '\'' +
                ", additionalText='" + additionalText + '\'' +
                ", commentId=" + commentId +
                ", date=" + date +
                '}';
    }
}
