package ru.mewory.photohost.service.vkapi;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.AuthorRepository;
import ru.mewory.photohost.dao.CommentsRepository;
import ru.mewory.photohost.dao.PostRepository;
import ru.mewory.photohost.exception.AllreadyHeldException;
import ru.mewory.photohost.model.Author;
import ru.mewory.photohost.model.socnet.*;
import ru.mewory.photohost.utils.UserUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by tookuk on 11/9/17.
 */
@Service
public class PostService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentsRepository commentsRepository;

    public void takeAndHold(Long postId) throws AllreadyHeldException {
        Comment comment = commentsRepository.findById(postId);
        if (!CommentStatus.FREE.equals(comment.getStatus())){
            throw new AllreadyHeldException(comment.getChangeUser());
        }
        comment.setChangeUser(UserUtils.getUsername());
        comment.setStatus(CommentStatus.IN_PROGRESS);
        commentsRepository.save(comment);
    }

    public Post savePost(List<SocnetDTO> data){
        SocnetDTO head = data.get(0);
        Post post = postRepository.findByTextAndSocnet(head.getText(), SocNet.INSTAGRAM);
        if (post == null){
            post = createPost(head);
        }
        postRepository.save(post);

        for (int i = 1; i < data.size(); i++){
            SocnetDTO e = data.get(i);
            String text = e.getText();
            if (post.getId() == null || CollectionUtils.isEmpty(commentsRepository.findByTextAndPost(text,post))) {
                saveComment(post, e);
            }
        }
        return post;
    }

    private Post createPost(SocnetDTO head) {
        Post post;
        post = new Post();
        post.setText(head.getText());
        post.setDate(head.getDate() == null ? new Date() : head.getDate());
        post.setSocnet(head.getSocnet() == null ? SocNet.INSTAGRAM : head.getSocnet());
        post.setNetId(head.getId());
        Author author = saveAuthor(head.getAuthor());
        post.setAuthor(author);
        return post;
    }

    private void saveComment(Post post, SocnetDTO e) {
        Comment comment;
        comment = new Comment();
        Author author = saveAuthor(e.getAuthor());
        comment.setText(e.getText());
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setNetId(e.getId());
        comment.setStatus(CommentStatus.FREE);
        commentsRepository.save(comment);
    }

    private Author saveAuthor(String authorName) {
        Author author = authorRepository.findByName(authorName);
        if (author == null) {
            author = new Author();
            author.setName(authorName);
            authorRepository.save(author);
        }
        return author;
    }

    public Post findNextPostAndFetchFreeComments(Long postId) {
        return null;
    }
}
