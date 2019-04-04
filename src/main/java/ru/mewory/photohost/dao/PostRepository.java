package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mewory.photohost.model.socnet.Post;
import ru.mewory.photohost.model.socnet.SocNet;

import java.util.List;

/**
 * Created by tookuk on 11/10/17.
 */
public interface PostRepository extends JpaRepository<Post,Long> {
    Post findByTextAndSocnet(String text, SocNet socNet);

    @Query("SELECT p FROM Post p JOIN FETCH p.comments WHERE p.id = ?1")
    Post findByIdAndFetchComments(Long id);

    @Query("SELECT p FROM Post p JOIN FETCH p.comments comm WHERE p.id = ?1 and comm.status = 'FREE'")
    Post findByIdAndFetchFreeComments(Long id);

    @Query("SELECT p FROM Post p WHERE EXISTS (from Comment c where c.status = 'FREE') AND p.id > ?1")
    List<Post> findPostsWhereExistsNonEdited(Long id);

    @Query("SELECT max(p.id) FROM Post p WHERE EXISTS (from Comment c where c.status = 'FREE' and c.post = p) AND p.id < ?1")
    Long findMaxPostIdWithFreeCommentsLessThenId(Long id);

    @Query("SELECT max(p.id) FROM Post p WHERE EXISTS (from Comment c where c.status = 'FREE' and c.post = p)")
    Long findMaxPostIdWithFreeComments();
}
