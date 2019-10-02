package ru.mewory.mediasort.controller;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.mewory.mediasort.model.socnet.Post;
import ru.mewory.mediasort.model.socnet.SocnetDTO;
import ru.mewory.mediasort.service.socnet.InstagramLoader;
import ru.mewory.mediasort.service.socnet.PostService;
import ru.mewory.mediasort.service.socnet.VkService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SocNetController {

    @Autowired
    private InstagramLoader instagramLoader;
    @Autowired
    private VkService vkService;
    @Autowired
    private PostService postService;

    Logger logger = LoggerFactory.getLogger(SocNetController.class);
    @Value("${self.domain}")
    private String selfDomain;
    @Value("${vk.appid}")
    private String appid;

    @RequestMapping(value = "/recievecode")
    public ResponseEntity<String> recieveCode(@RequestParam String code) {
        logger.info("code recieved: " + code);
        return ResponseEntity.ok(code);
    }

    @RequestMapping(value = "/vkloadauth")
    public RedirectView vkLoadAuth() {
        String authUrl = "https://oauth.vk.com/authorize?client_id=" + appid
                + "&display=page&redirect_uri="
                + selfDomain + "/vkload&scope=wall,photos,video&response_type=code&v=5.101";
        return new RedirectView(authUrl);
    }

    @RequestMapping(value = {"/instagramloader"})
    public ModelAndView instaloader(@RequestParam Map<String, String> allRequestParams) {
        return new ModelAndView("instagramloader");
    }

    @RequestMapping(method = POST, value = "loadFromInstagram")
    public ResponseEntity<String> loadFromInstagram(@RequestBody Map<String, String> allRequestParams) {
        Post instagramRef = instagramLoader.load(allRequestParams.get("instagramRef"));
        if (instagramRef != null) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("FAIL", HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/vkload")
    public ModelAndView vkload(@RequestParam Map<String, String> allRequestParams) throws InterruptedException, ClientException, ApiException {
        ModelAndView mav = new ModelAndView("vkload");

        if (allRequestParams.get("code") != null) {
            vkService.setAuthCode(allRequestParams.get("code"));
        }

        int offset = getOffset(allRequestParams);
        mav.addObject("offset", offset);
        List<List<SocnetDTO>> postsWithComments = vkService.getPostsWithComments(offset);
        if (!CollectionUtils.isEmpty(postsWithComments)) {
            List<Post> posts = new ArrayList<>();
            postsWithComments.forEach(socnetDTOS -> posts.add(postService.savePost(socnetDTOS)));
            mav.addObject("posts", posts);
        }
        return mav;
    }

    private int getOffset(@RequestParam Map<String, String> allRequestParams) {
        int offset = 0;
        try {
            offset = Integer.parseInt(allRequestParams.get("offset"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offset;
    }


}
