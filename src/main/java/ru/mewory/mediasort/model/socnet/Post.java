package ru.mewory.mediasort.model.socnet;

import ru.mewory.mediasort.model.Author;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by tookuk on 11/10/17.
 */
@Entity
@Table(name = "posts",
        indexes = {@Index(name = "IDX_TEXT_SOCNET", columnList = "text,socnet")})
public class Post {
    private static final int COMMENT_MAX_LENGTH = 12000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocNet socnet;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @OrderBy("id")
    private Set<Comment> comments = new TreeSet<>();

    @Column(length = COMMENT_MAX_LENGTH)
    private String text;

    private Long netId;

    private Date date;

    private String postLink;


    public String getPostLink() {
        return postLink;
    }

    public Post setPostLink(String postLink) {
        this.postLink = postLink;
        return this;
    }

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
        if (text.length() > COMMENT_MAX_LENGTH) {
            this.text = text.substring(0, COMMENT_MAX_LENGTH);
        }
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
