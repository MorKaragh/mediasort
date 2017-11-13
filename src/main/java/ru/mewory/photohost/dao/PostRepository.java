package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mewory.photohost.model.socnet.Post;
import ru.mewory.photohost.model.socnet.SocNet;

/**
 * Created by tookuk on 11/10/17.
 */
public interface PostRepository extends JpaRepository<Post,Long> {
    Post findById(Long id);
    Post findByTextAndSocnet(String text, SocNet socNet);
    @Query("SELECT p FROM Post p JOIN FETCH p.comments WHERE p.id = ?1")
    Post findByIdAndFetchComments(Long id);
}
