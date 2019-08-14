package ru.mewory.photohost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.CommentsRepository;
import ru.mewory.photohost.dao.PostRepository;
import ru.mewory.photohost.model.JournalElement;
import ru.mewory.photohost.model.socnet.Post;

import java.util.ArrayList;
import java.util.List;

@Service
public class JournalService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    public List<JournalElement> getJournal() {
        List<JournalElement> result = new ArrayList<>();
        List<Post> posts = postRepository.findTop100ByOrderByDateDesc();
        for (Post p : posts) {
            result.add(
                    getSingleElement(p)
            );
        }
        return result;
    }

    public JournalElement getSingleElement(Post p) {
        return new JournalElement()
                .setPost(p)
                .setProcessed(commentsRepository.countProcessed(p.getId()))
                .setUnprocessed(commentsRepository.countUnprocessed(p.getId()));
    }

}
