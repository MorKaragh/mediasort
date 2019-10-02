package ru.mewory.mediasort.service.socnet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mewory.mediasort.dao.*;
import ru.mewory.mediasort.exception.AllreadyHeldException;
import ru.mewory.mediasort.model.Author;
import ru.mewory.mediasort.model.Record;
import ru.mewory.mediasort.model.dictionaries.DictsEditHistory;
import ru.mewory.mediasort.model.socnet.*;
import ru.mewory.mediasort.service.RecordService;
import ru.mewory.mediasort.utils.UserUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
    private RecordService recordService;
    @Autowired
    private DictEditHistoryRepository dictEditHistoryRepository;

    public Comment takeAndHold(Long postId) throws AllreadyHeldException {
        Comment comment = commentsRepository.findById(postId).get();
        if (!CommentStatus.FREE.equals(comment.getStatus()) && !CommentStatus.IN_PROGRESS.equals(comment.getStatus())){
            Record record = recordService.loadByCommentId(comment.getId());
            throw new AllreadyHeldException(comment, record);
        }
        comment.setChangeUser(UserUtils.getUsername());
        comment.setStatus(CommentStatus.IN_PROGRESS);
        commentsRepository.save(comment);
        return comment;
    }

    @Transactional
    public Post savePost(List<SocnetDTO> data){
        SocnetDTO head = data.get(0);
        Post post = null;
        if (head.getId() != null && SocNet.VK.equals(head.getSocnet())) {
            post = postRepository.findByNetId(head.getId());
        } else if (StringUtils.isNotBlank(head.getLink())) {
            post = postRepository.findByPostLink(head.getLink());
        }
        if (post == null) {
            post = postRepository.findByTextAndSocnet(head.getText(), SocNet.INSTAGRAM);
        }
        if (post == null && StringUtils.isNotBlank(head.getText())) {
            post = postRepository.findByTextAndSocnet(head.getText(), SocNet.VK);
        }
        if (post == null){
            post = createPost(head);
        }
        post.setPostLink(head.getLink());
        post.setDate(head.getDate());
        postRepository.save(post);

        for (int i = 1; i < data.size(); i++){
            SocnetDTO e = data.get(i);
            String text = e.getText();
            if (post.getId() == null || CollectionUtils.isEmpty(
                    commentsRepository.findByTextAndPostAndAuthorName(text,post,e.getAuthor()))) {
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
        comment.setDate(e.getDate() == null ? new Date() : e.getDate());
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

    private Post findNextPostAndFetchAllComments(Long postId) {
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
        Comment c = commentsRepository.findById(commentId).get();
        if (Arrays.asList(CommentStatus.NO_PLACE, CommentStatus.NO_THEME).contains(s)){
            c.setStatus(s);
            c.setChangeUser(UserUtils.getUsername());
            commentsRepository.save(c);
        }
    }

    public Post getPostForEditFromReport(String startDate, String endDate, String theme, String location, String description, String address) {
        try {
            List<Record> records = recordRepository.findForReport(
                    location,
                    theme,
                    description,
                    DateUtils.parseDate(startDate, "dd.MM.yyyy"),
                    DateUtils.parseDate(endDate, "dd.MM.yyyy"),
                    address);
            if (!CollectionUtils.isEmpty(records)){
                List<Long> recordIds = records.stream().map(Record::getCommentId).collect(Collectors.toList());
                Set<Comment> comments = commentsRepository.findByIdIn(recordIds);
                Post post = new Post();
                post.setComments(comments);
                return post;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Post getPostById(Long id){
        return postRepository.findByIdAndFetchComments(id);
    }

    public Post getClosestPostToEdit(String prevPostId) {
        if (prevPostId == null) {
            return findNextPostAndFetchAllComments(null);
        } else {
            return findNextPostAndFetchAllComments(Long.valueOf(prevPostId));
        }
    }

    @Transactional
    public void deletePost(Long postId) {
        for (Comment c : commentsRepository.findByPostId(postId)) {
            Record record = recordRepository.findByCommentId(c.getId());
            if (record != null) {
                List<DictsEditHistory> dictEdits = dictEditHistoryRepository.findByRecord(record);
                if (CollectionUtils.isNotEmpty(dictEdits)) {
                    for (DictsEditHistory history : dictEdits) {
                        dictEditHistoryRepository.delete(history);
                    }
                }
                recordRepository.delete(record);
            }
            commentsRepository.delete(c);
        }
        postRepository.deleteById(postId);
    }
}
