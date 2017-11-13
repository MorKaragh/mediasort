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
import ru.mewory.photohost.model.Author;
import ru.mewory.photohost.model.socnet.*;

import java.util.Date;

/**
 * Created by tookuk on 11/9/17.
 */
@Service
public class InstagramService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentsRepository commentsRepository;

    public Post parseData(InstagramData data){
        if (data.getData() == null) throw new NullPointerException();
        Document parse = Jsoup.parse(data.getData());
        Elements allElements = parse.getElementsByAttributeValueMatching("class",".*notranslate.*");

        Element postHead = allElements.get(0);
        Post post = postRepository.findByTextAndSocnet(postHead.nextElementSibling().text(), SocNet.INSTAGRAM);
        if (post == null){
            post = new Post();
            post.setText(postHead.nextElementSibling().text());
            post.setDate(new Date());
            post.setSocnet(SocNet.INSTAGRAM);
            String authorName = postHead.text();
            Author author = saveAuthor(authorName);
            post.setAuthor(author);
        }
        postRepository.save(post);

        for (int i = 1; i < allElements.size(); i++){
            Element e = allElements.get(i);
            if (e.text().startsWith("@")) continue;
            Element textElement  = e.nextElementSibling();
            if (textElement != null) {
                String authorName = e.text();
                Author author = saveAuthor(authorName);
                String text = textElement.text();
                if (post.getId() == null || CollectionUtils.isEmpty(commentsRepository.findByTextAndPost(text,post))) {
                    saveComment(post, author, text);
                }
            }

        }
        return post;
    }

    private void saveComment(Post post, Author author, String text) {
        Comment comment;
        comment = new Comment();
        comment.setText(text);
        comment.setAuthor(author);
        comment.setPost(post);
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
}
