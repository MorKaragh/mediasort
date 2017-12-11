package ru.mewory.photohost.service.socnet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.*;
import ru.mewory.photohost.exception.AllreadyHeldException;
import ru.mewory.photohost.model.Author;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.socnet.*;
import ru.mewory.photohost.service.RecordService;
import ru.mewory.photohost.utils.UserUtils;

import java.util.Arrays;
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
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    RecordService recordService;

    public void takeAndHold(Long postId) throws AllreadyHeldException {
        Comment comment = commentsRepository.findById(postId);
        if (!CommentStatus.FREE.equals(comment.getStatus())){
            Record record = recordService.loadByCommentId(comment.getId());
            throw new AllreadyHeldException(comment, record);
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
        comment.setStatus(StringUtils.isBlank(e.getText()) ? CommentStatus.NO_THEME : CommentStatus.FREE);
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
        Long maxPostIdWithFreeComments;
        if (postId == null) {
            maxPostIdWithFreeComments = postRepository.findMaxPostIdWithFreeComments();
        } else {
            maxPostIdWithFreeComments = postRepository.findMaxPostIdWithFreeCommentsLessThenId(postId);
        }
        return postRepository.findByIdAndFetchFreeComments(maxPostIdWithFreeComments);
    }

    public Post findNextPostAndFetchAllComments(Long postId) {
        Long maxPostIdWithFreeComments;
        if (postId == null) {
            maxPostIdWithFreeComments = postRepository.findMaxPostIdWithFreeComments();
        } else {
            maxPostIdWithFreeComments = postRepository.findMaxPostIdWithFreeCommentsLessThenId(postId);
        }
        return postRepository.findByIdAndFetchComments(maxPostIdWithFreeComments);
    }

    public void release(Long commentId) {
        Comment byId = commentsRepository.findByIdAndStatus(commentId,CommentStatus.IN_PROGRESS);
        if (byId != null){
            byId.setStatus(CommentStatus.FREE);
            byId.setChangeUser(null);
            commentsRepository.save(byId);
        }
    }

    public void setTrashStatus(Long commentId, String status) {
        CommentStatus s = CommentStatus.valueOf(status);
        Comment c = commentsRepository.findById(commentId);
        if (Arrays.asList(CommentStatus.NO_PLACE, CommentStatus.NO_THEME).contains(s)
//                && !c.getStatus().equals(CommentStatus.DONE)
                ){
            c.setStatus(s);
            c.setChangeUser(UserUtils.getUsername());
            commentsRepository.save(c);
        }
    }
}
