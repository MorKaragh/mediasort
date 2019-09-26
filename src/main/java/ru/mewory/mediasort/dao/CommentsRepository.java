package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mewory.mediasort.model.socnet.Comment;
import ru.mewory.mediasort.model.socnet.CommentStatus;
import ru.mewory.mediasort.model.socnet.Post;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface CommentsRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByText(String text);


    @Query("SELECT c FROM Comment c, Author a WHERE c.author = a AND c.text = ?1 AND c.post = ?2 AND a.name = ?3")
    List<Comment> findByTextAndPostAndAuthorName(String text,Post p, String authorName);
    List<Comment> findByPost(Post post);
    Comment findByIdAndStatus(Long commentId, CommentStatus inProgress);
    Set<Comment> findByIdIn(List<Long> recordIds);

    @Query("SELECT COUNT(1) FROM Comment WHERE post_id = ?1 AND status not in ('FREE','IN_PROGRESS')")
    int countProcessed(Long id);

    @Query("SELECT COUNT(1) FROM Comment WHERE post_id = ?1 AND status in ('FREE','IN_PROGRESS')")
    int countUnprocessed(Long id);

    @Query("SELECT COUNT(1) FROM Comment c, Post p WHERE c.post = p AND p.socnet = 'INSTAGRAM' AND c.date BETWEEN ?1 AND ?2")
    int countInstagramComments(Date startDate, Date endDate);

    @Query("SELECT COUNT(1) FROM Comment c, Post p WHERE c.post = p AND p.socnet = 'VK' AND c.date BETWEEN ?1 AND ?2")
    int countVkComments(Date startDate, Date endDate);
}
