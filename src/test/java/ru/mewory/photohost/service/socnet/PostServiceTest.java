package ru.mewory.photohost.service.socnet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mewory.photohost.dao.PostRepository;
import ru.mewory.photohost.model.socnet.Post;
import ru.mewory.photohost.model.socnet.SocNet;
import ru.mewory.photohost.model.socnet.SocnetDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Test
    public void savePost() {

        List<SocnetDTO> dtos = new ArrayList<>();

        dtos.add(new SocnetDTO("author1","text1")
        .setSocnet(SocNet.INSTAGRAM)
        .setLink("link1"));

        dtos.add(new SocnetDTO("author2","text2")
        .setSocnet(SocNet.INSTAGRAM)
        .setLink("link2"));

        dtos.add(new SocnetDTO("author3","text3")
        .setSocnet(SocNet.INSTAGRAM)
        .setLink("link3"));

        Post post = postService.savePost(dtos);

        assertEquals(postRepository.findAll().size(), 1);

        Post loadedPost = postRepository.findByIdAndFetchComments(post.getId());

        assertNotNull(loadedPost);
        assertEquals("author1", loadedPost.getAuthor().getName());
        assertEquals("text1", loadedPost.getText());
        assertEquals(2, loadedPost.getComments().size());

    }
}