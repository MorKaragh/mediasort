package ru.mewory.photohost.model.socnet;

import ru.mewory.photohost.model.Author;

import javax.persistence.*;

/**
 * Created by tookuk on 11/10/17.
 */
@Entity
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(length = 8000)
    private String text;
    private Long netId;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "author_id")
    private Author author;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;
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
        return "Comment{" + "\n" +
                "id=" + id + "\n" +
                ", post=" + post +
                ", text='" + text + '\'' + "\n" +
                ", netId=" + netId + "\n" +
                ", author=" + author + "\n" +
                ", status=" + status + "\n" +
                '}';
    }
}
