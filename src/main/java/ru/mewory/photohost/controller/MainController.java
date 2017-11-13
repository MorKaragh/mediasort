package ru.mewory.photohost.controller;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.photohost.dao.*;
import ru.mewory.photohost.model.*;
import ru.mewory.photohost.model.socnet.*;
import ru.mewory.photohost.service.ImageSaveService;
import ru.mewory.photohost.service.RecordSaveService;
import ru.mewory.photohost.service.SimpleReportService;
import ru.mewory.photohost.service.vkapi.InstagramService;
import ru.mewory.photohost.service.vkapi.VkService;

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
    private SimpleReportService reportService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private VkService vkService;
    @Autowired
    private InstagramService instagramService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @GetMapping("/test")
    public String test() throws ClientException, ApiException {
        List<Post> all = postRepository.findAll();
        all.toString();
        List<Comment> byPost = commentsRepository.findByPost(all.get(0));
        byPost.toString();
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/parseInstagram")
    public ResponseEntity<Map<String,String>> parseInstagram(@RequestBody InstagramData instagramData) throws IOException {
        Map<String,String> result = new HashMap<>();
        instagramService.parseData(instagramData);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping("/instaload")
    public ModelAndView instaload(){
        ModelAndView mav = new ModelAndView("instaload");
        return mav;
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

    @RequestMapping(value = {"/record"})
    public ModelAndView record(Model model){
        ModelAndView mav = new ModelAndView("record");
        List<Author> authors = authorRepository.findAll();
        mav.addObject("authors", authors);
        List<Location> locations = locationRepository.findAll();
        mav.addObject("locations", locations);
        List<Theme> themes = themeRepository.findAll();
        mav.addObject("themes", themes);
        return mav;
    }

    @RequestMapping(method=RequestMethod.GET, value="report")
    public @ResponseBody ModelAndView getReport(@RequestParam Map<String,String> allRequestParams){
        System.out.println(allRequestParams.toString());
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

    public class SearchRequest {
        private Map<String, String> params;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/send")
    public String upload(@RequestBody Image img) throws IOException {
        imageSaveService.save(img);
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST, value = "sendRecord")
    public ResponseEntity<Map<String,String>> sendRecord(@RequestBody Record record) throws IOException {
        recordSaveService.save(record);
        Map<String,String> result = new HashMap<>();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method=RequestMethod.GET, value="/rectags")
    public @ResponseBody List<Tag> recordTags(){
        return tagRepository.findAll();
    }


}
