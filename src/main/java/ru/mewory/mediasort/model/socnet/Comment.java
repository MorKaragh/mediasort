package ru.mewory.mediasort.model.socnet;

import ru.mewory.mediasort.model.Author;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tookuk on 11/10/17.
 */
@Entity
@Table(name="comments",
        indexes = {@Index(columnList = "date"),
                @Index(columnList = "post_id"),
                @Index(columnList = "status")})
public class Comment implements Comparable {
    private static final int TEXT_MAX_LENGTH = 8000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(length = TEXT_MAX_LENGTH)
    private String text;

    private Long netId;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "author_id")
    private Author author;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private String changeUser;

    public String getChangeUser() {
        return changeUser;
    }

    public void setChangeUser(String changeUser) {
        this.changeUser = changeUser;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post postId) {
        this.post = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text.length() > TEXT_MAX_LENGTH) {
            this.text = text.substring(0, TEXT_MAX_LENGTH);
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


    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", post=" + post +
                ", text='" + text + '\'' +
                ", netId=" + netId +
                ", author=" + author +
                ", status=" + status +
                ", date=" + date +
                ", changeUser='" + changeUser + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Comment && ((Comment) o).getDate() != null) {
            return date.compareTo(((Comment) o).getDate());
        }
        return -1;
    }
}
