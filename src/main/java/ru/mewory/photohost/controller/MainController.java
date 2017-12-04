package ru.mewory.photohost.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.photohost.dao.*;
import ru.mewory.photohost.exception.AllreadyHeldException;
import ru.mewory.photohost.model.*;
import ru.mewory.photohost.model.socnet.*;
import ru.mewory.photohost.service.ImageSaveService;
import ru.mewory.photohost.service.RecordSaveService;
import ru.mewory.photohost.service.ReportService;
import ru.mewory.photohost.service.socnet.InstagramParser;
import ru.mewory.photohost.service.socnet.PostService;
import ru.mewory.photohost.service.socnet.VkService;

import java.io.IOException;
import java.util.*;

/**
 * Created by tookuk on 9/3/17.
 */
@Controller
@EnableAutoConfiguration
public class MainController {

    @Autowired
    private ImageSaveService imageSaveService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private RecordSaveService recordSaveService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private VkService vkService;
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @GetMapping("/test")
    public String test() throws ClientException, ApiException, InterruptedException {
        Long maxPostIdWithFreeComments = postRepository.findMaxPostIdWithFreeComments();
        maxPostIdWithFreeComments.toString();
        Post byId = postRepository.findByIdAndFetchComments(2L);
        List<Comment> byPostId = commentsRepository.findByPostId(2L);
        return "login";
    }

    @RequestMapping(value = {"/record"})
    public ModelAndView record(@RequestParam Map<String,String> allRequestParams){
        ModelAndView mav = new ModelAndView("record");
        List<Location> locations = locationRepository.findAll();
        mav.addObject("locations", locations);
        List<Theme> themes = themeRepository.findAll();
        mav.addObject("themes", themes);

        String postId = allRequestParams.get("postId");
//        Post post = postService.findNextPostAndFetchFreeComments(postId != null ? Long.valueOf(postId) : null);
        Post post = postService.findNextPostAndFetchAllComments(postId != null ? Long.valueOf(postId) : null);
        if (post == null){
            mav.setViewName("instaload");
        } else {
            mav.addObject("post", post);
        }
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, value = "sendRecord")
    public ResponseEntity<Map<String,String>> sendRecord(@RequestBody Record record) throws IOException {
        recordSaveService.save(record);
        Map<String,String> result = new HashMap<>();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/parseInstagram")
    public ModelAndView parseInstagram(@RequestBody InstagramData instagramData) throws IOException {
        ModelAndView modelAndView = new ModelAndView("parsedpost");
        List<SocnetDTO> parsed = InstagramParser.parse(instagramData.getData());
        modelAndView.addObject("post",parsed.get(0));
        modelAndView.addObject("comments",parsed.subList(1,parsed.size()));
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setStatus")
    public ResponseEntity<String>  setStatus(@RequestBody Map<String,String> allRequestParams){
        JsonObject jsonObject = new JsonObject();
        postService.setTrashStatus(Long.valueOf(allRequestParams.get("commentId")),allRequestParams.get("status"));
        return new ResponseEntity<>(new Gson().toJson(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/takeToWork")
    public ResponseEntity<String>  takeToWork(@RequestBody Map<String,String> allRequestParams){
        JsonObject jsonObject = new JsonObject();
        try {
            if (allRequestParams.get("commentId") != null) {
                postService.takeAndHold(Long.valueOf(allRequestParams.get("commentId")));
                jsonObject.addProperty("available","true");
            } else {
                jsonObject.addProperty("error","нет такого комментария");
                jsonObject.addProperty("available","false");
            }
        } catch (AllreadyHeldException e){
            jsonObject.addProperty("error","этот комментарий уже обработан");
            jsonObject.addProperty("available","false");
            jsonObject.addProperty("status", e.getStatus().toString());
        }
        return new ResponseEntity<>(new Gson().toJson(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/release")
    public ResponseEntity<String>  release(@RequestBody Map<String,String> allRequestParams){
        JsonObject jsonObject = new JsonObject();
        if (allRequestParams.get("commentId") != null) {
            postService.release(Long.valueOf(allRequestParams.get("commentId")));
        }
        return new ResponseEntity<>(new Gson().toJson(jsonObject),HttpStatus.OK);
    }

    @RequestMapping("/instaload")
    public ModelAndView instaload(){
        ModelAndView mav = new ModelAndView("instaload");
        return mav;
    }

    @RequestMapping(method=RequestMethod.GET, value="/vkload")
    public ModelAndView vkload(@RequestParam Map<String,String> allRequestParams) throws InterruptedException, ClientException, ApiException {
        ModelAndView mav = new ModelAndView("vkload");
        int offset = getOffset(allRequestParams);
        mav.addObject("offset",offset);
        List<List<SocnetDTO>> postsWithComments = vkService.getPostsWithComments(offset);
        if (!CollectionUtils.isEmpty(postsWithComments)){
            List<Post> posts = new ArrayList<>();
            postsWithComments.forEach(socnetDTOS -> posts.add(postService.savePost(socnetDTOS)));
            mav.addObject("posts",posts);
        }
        return mav;
    }

    private int getOffset(@RequestParam Map<String, String> allRequestParams) {
        int offset = 0;
        try {
            offset = Integer.parseInt(allRequestParams.get("offset"));
        } catch (Exception e){}
        return offset;
    }

    @RequestMapping(method=RequestMethod.GET, value="report")
    public @ResponseBody ModelAndView getReport(@RequestParam Map<String,String> allRequestParams){
        ModelAndView mav = new ModelAndView("report");
        List<Record> records = reportService.getReport(allRequestParams);
        mav.addObject("report",records);
        List<Location> locations = locationRepository.findAll();
        mav.addObject("locations", locations);
        List<Theme> themes = themeRepository.findAll();
        mav.addObject("themes", themes);
        mav.addObject("startDate",allRequestParams.get("startDate"));
        mav.addObject("endDate",allRequestParams.get("endDate"));
        return mav;
    }

    @RequestMapping("/savePost")
    public ResponseEntity<String> savePost(@RequestBody InstagramData instagramData){
        ModelAndView mav = new ModelAndView("instaload");
        List<SocnetDTO> parsed = InstagramParser.parse(instagramData.getData());
        Post post = postService.savePost(parsed);
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }

    @RequestMapping("/photo")
    public ModelAndView photo(Model model) {
        ModelAndView mav = new ModelAndView("photo");
        List<Location> locations = locationRepository.findAll();
        List<Author> authors = authorRepository.findAll();
        mav.addObject("locations", locations);
        mav.addObject("authors", authors);
        return mav;
    }

    @RequestMapping(value = {"/","/login"})
    public String login(Model model) {
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/send")
    public String upload(@RequestBody Image img) throws IOException {
        imageSaveService.save(img);
        return "index";
    }

    @RequestMapping(method=RequestMethod.GET, value="/rectags")
    public @ResponseBody List<Tag> recordTags(){
        return tagRepository.findAll();
    }


}
