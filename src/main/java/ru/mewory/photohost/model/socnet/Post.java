package ru.mewory.photohost.model.socnet;

import ru.mewory.photohost.model.Author;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tookuk on 11/10/17.
 */
@Entity
@Table(name = "posts",
        indexes = {@Index(name = "IDX_TEXT_SOCNET", columnList = "text,socnet")})
public class Post {
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
    private Set<Comment> comments = new HashSet<>();
    @Column(length = 12000)
    private String text;
    private Long netId;
    private Date date;

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
        if (text.length() > 12000) {
            this.text = text.substring(0, 11999);
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
