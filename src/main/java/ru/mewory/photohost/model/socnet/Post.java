package ru.mewory.photohost.model.socnet;

import ru.mewory.photohost.model.Author;

import javax.persistence.*;
import java.util.*;

/**
 * Created by tookuk on 11/10/17.
 */
@Entity
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 4000)
    private String text;
    private Long netId;
    @Enumerated(EnumType.STRING)
    private SocNet socnet;
    private Date date;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "author_id")
    private Author author;


    @OneToMany(mappedBy = "post", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }


    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNetId() {
        return netId;
    }

    public void setNetId(Long netId) {
        this.netId = netId;
    }

    public SocNet getSocnet() {
        return socnet;
    }

    public void setSocnet(SocNet socnet) {
        this.socnet = socnet;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
