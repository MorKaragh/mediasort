package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.photohost.model.socnet.Comment;
import ru.mewory.photohost.model.socnet.CommentStatus;
import ru.mewory.photohost.model.socnet.Post;

import java.util.List;
import java.util.Set;

/**
 * Created by tookuk on 11/11/17.
 */
public interface CommentsRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByText(String text);
    List<Comment> findByTextAndPost(String text,Post post);
    List<Comment> findByPost(Post post);
    Comment findByIdAndStatus(Long commentId, CommentStatus inProgress);
    Set<Comment> findByIdIn(List<Long> recordIds);
}
