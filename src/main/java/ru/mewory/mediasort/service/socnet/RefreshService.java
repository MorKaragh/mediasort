package ru.mewory.mediasort.service.socnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.mediasort.model.socnet.Post;
import ru.mewory.mediasort.model.socnet.SocnetDTO;

import java.util.List;

@Service
public class RefreshService {

    @Autowired
    private VkService vkService;
    @Autowired
    private InstagramLoader instagramLoader;
    @Autowired
    private PostService postService;

    public Post refresh(Long vkId, String instagramLink) throws Exception {
        if (vkId != null) {
            List<List<SocnetDTO>> post = vkService.getPostWithCommentsById(vkId);
            if (post.size() != 1) {
                throw new Exception("с данным ID больше одного поста!");
            }
            return postService.savePost(post.get(0));
        } else if (instagramLink != null) {
            return instagramLoader.load(instagramLink);
        }
        return null;
    }

}
