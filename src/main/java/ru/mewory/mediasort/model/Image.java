package ru.mewory.mediasort.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="images")
public class Image {

    @JsonProperty("location")
    private String location;

    @JsonProperty("author")
    private String author;

    private String path;

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @JsonProperty("tags")
    @Transient
    private List<String> tags;

    @JsonProperty("img")
    @Transient
    private String encoded;


    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public List<String> getTags() {
        if (tags == null){
            tags = new ArrayList<>();
        }
        return tags;
    }

    @Transient
    public String getPic(){
        if(encoded != null && encoded.contains(",")) {
            return encoded.split(",")[1];
        }
        return null;
    }

    @Transient
    public String getExtension(){
        if(encoded != null && encoded.contains(",")) {
            String s = encoded.split(",")[0];
            if(s.toLowerCase().contains("jpg") || s.toLowerCase().contains("jpeg")){
                return "jpg";
            } else if (s.toLowerCase().contains("png")){
                return "png";
            }
        }
        return null;
    }

    public String getLocation() {
        return location;
    }

    public String getAuthor() {
        return author;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Transient
    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }
}
