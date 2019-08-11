package ru.mewory.photohost.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.photohost.dao.*;
import ru.mewory.photohost.exception.AllreadyHeldException;
import ru.mewory.photohost.model.*;
import ru.mewory.photohost.model.dictionaries.Location;
import ru.mewory.photohost.model.dictionaries.Theme;
import ru.mewory.photohost.model.report.ReportTheme;
import ru.mewory.photohost.model.socnet.Comment;
import ru.mewory.photohost.model.socnet.Post;
import ru.mewory.photohost.model.socnet.SocnetDTO;
import ru.mewory.photohost.service.*;
import ru.mewory.photohost.service.socnet.InstagramLoader;
import ru.mewory.photohost.service.socnet.PostService;
import ru.mewory.photohost.service.socnet.VkService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by tookuk on 9/3/17.
 */
@Controller
@EnableAutoConfiguration
public class MainController {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    @Autowired
    private ImageSaveService imageSaveService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private RecordService recordService;
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
    private RecordRepository recordRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private InstagramLoader instagramLoader;
    @Autowired
    private JournalService journalService;
    @Autowired
    private DictEditService dictService;
    @Autowired
    private DictEditRepository dictEditRepository;

    @GetMapping("/test")
    public String test() {

        for (Record r : recordRepository.findAll()) {
            System.out.println(r.toString());
            System.out.println(commentsRepository.findById(r.getCommentId()).toString());
        }

        return "report";
    }

    @RequestMapping(value = {"/record"})
    public ModelAndView record(@RequestParam Map<String,String> allRequestParams){
        ModelAndView mav = new ModelAndView("record");
        fillDictionaries(mav);

        Post post;
        if (allRequestParams.get("strict") != null) {
            post = postService.getPostById(Long.valueOf(allRequestParams.get("postId")));
        } else {
            post = postService.getPost(
                    allRequestParams.get("postId")
            );
        }

        mav.addObject("realpost","true");
        if (post == null){
            mav.setViewName("journal");
        } else {
            mav.addObject("post", post);
        }
        return mav;
    }

    @RequestMapping(method = POST, value = {"/reportedit"})
    public ModelAndView reportedit(@RequestBody Map<String,String> allRequestParams){
        ModelAndView mav = new ModelAndView("reportedit");
        fillDictionaries(mav);

        Post post = postService.getPostForEditFromReport(
                allRequestParams.get("startDate"),
                allRequestParams.get("endDate"),
                allRequestParams.get("theme"),
                allRequestParams.get("location"),
                allRequestParams.get("description"),
                allRequestParams.get("address")
        );

        mav.addObject("realpost","false");
        mav.addObject("post", post);
        return mav;
    }

    private void fillDictionaries(ModelAndView mav) {
        List<Location> locations = locationRepository.findAll();
        locations.sort(Comparator.comparing(Location::getName));
        mav.addObject("locations", locations);
        List<Theme> themes = themeRepository.findAll();
        themes.sort(Comparator.comparing(Theme::getName));
        mav.addObject("themes", themes);
    }


    @RequestMapping(method = GET, value = "report")
    public @ResponseBody ModelAndView getReport(@RequestParam Map<String,String> allRequestParams) throws ParseException {
        ModelAndView mav = new ModelAndView("report");

        Date startDate = null;
        if (allRequestParams.get("startDate") != null) {
            startDate = SIMPLE_DATE_FORMAT.parse(allRequestParams.get("startDate"));
        }
        Date endDate = null;
        if (allRequestParams.get("endDate") != null) {
            endDate = SIMPLE_DATE_FORMAT.parse(allRequestParams.get("endDate"));
        }

        List<ReportTheme> records = reportService.getReport(startDate,endDate);

        mav.addObject("countVedomstva",reportService.getVedomstvaCount(startDate,endDate));
        mav.addObject("countDistinctUsers",reportService.getDistinctUsersCount(startDate,endDate));
        int totalVkCount = reportService.getTotalVkCount(startDate, endDate);
        int totalInstagramCount = reportService.getTotalInstagramCount(startDate, endDate);
        mav.addObject("countTotal", totalVkCount + totalInstagramCount);
        mav.addObject("countTotalVk", totalVkCount);
        mav.addObject("countTotalInstagram", totalInstagramCount);



        mav.addObject("report",records);
        mav.addObject("startDate",allRequestParams.get("startDate"));
        mav.addObject("endDate",allRequestParams.get("endDate"));

        return mav;
    }

    @RequestMapping(method = GET, value = "dictionaries")
    public @ResponseBody ModelAndView dictionaries(@RequestParam Map<String,String> allRequestParams){
        ModelAndView mav = new ModelAndView("dictionaries");
        fillDictionaries(mav);
        mav.addObject("transactions",dictEditRepository.findAll(new Sort(Sort.Direction.DESC,"id")));
        return mav;
    }

    @RequestMapping(method = POST, value = "changeDicts")
    public ResponseEntity<Map<String,String>> changeDicts(@RequestBody Map<String, String> allRequestParams) throws IOException {
        if (StringUtils.isNotEmpty(allRequestParams.get("oldLocation")) && StringUtils.isNotEmpty(allRequestParams.get("newLocation"))) {
            dictService.changeDictionary(allRequestParams.get("oldLocation"),allRequestParams.get("newLocation"),"Место");
        }
        if (StringUtils.isNotEmpty(allRequestParams.get("oldTheme")) && StringUtils.isNotEmpty(allRequestParams.get("newTheme"))) {
            dictService.changeDictionary(allRequestParams.get("oldTheme"),allRequestParams.get("newTheme"),"Тема");
        }
        Map<String,String> result = new HashMap<>();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(method = POST, value = "rollbackDicts")
    public ResponseEntity<Map<String,String>> rollbackDicts(@RequestBody Map<String, String> allRequestParams) throws IOException {
        dictService.rollbackTransaction();
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
    }



    @RequestMapping(method = GET, value = {"journal", "/"})
    public @ResponseBody
    ModelAndView getJournal(@RequestParam Map<String, String> allRequestParams) {
        ModelAndView mav = new ModelAndView("journal");
        List<JournalElement> posts = journalService.getJournal();
        mav.addObject("posts", posts);
        return mav;
    }

    @RequestMapping(method = POST, value = "sendRecord")
    public ResponseEntity<Map<String,String>> sendRecord(@RequestBody Record record) throws IOException {
        recordService.save(record);
        Map<String,String> result = new HashMap<>();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = {"/instagramloader"})
    public ModelAndView instaloader(@RequestParam Map<String, String> allRequestParams) {
        return new ModelAndView("instagramloader");
    }

    @RequestMapping(method = POST, value = "loadFromInstagram")
    public ResponseEntity<String> loadFromInstagram(@RequestBody Map<String, String> allRequestParams) {
        instagramLoader.load(allRequestParams.get("instagramRef"));
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @RequestMapping(method = POST, value = "/setStatus")
    public ResponseEntity<String>  setStatus(@RequestBody Map<String,String> allRequestParams){
        JsonObject jsonObject = new JsonObject();
        postService.setTrashStatus(Long.valueOf(allRequestParams.get("commentId")),allRequestParams.get("status"));
        return new ResponseEntity<>(new Gson().toJson(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(method = POST, value = "/takeToWork")
    public ResponseEntity<String> takeToWork(@RequestBody Map<String,String> allRequestParams){
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
            jsonObject.addProperty("recordText", e.getRecord() != null ? e.getRecord().getDescription() : "");
            jsonObject.addProperty("recordTags", e.getRecord() != null ? String.join("|", e.getRecord().getTags()) : "");
            jsonObject.addProperty("recordLocation", e.getRecord() != null ? e.getRecord().getLocation() : "");
            jsonObject.addProperty("recordTheme", e.getRecord() != null ? e.getRecord().getTheme() : "");
            jsonObject.addProperty("additionalText", e.getRecord() != null ? e.getRecord().getAdditionalText() : "");
            jsonObject.addProperty("isVedomstvo", e.getComment() != null ? extractVedomstvoFlag(e) : "false");
        }
        return new ResponseEntity<>(new Gson().toJson(jsonObject), HttpStatus.OK);
    }

    private String extractVedomstvoFlag(AllreadyHeldException e) {
        return Optional.ofNullable(e.getComment())
                .map(Comment::getAuthor)
                .map(Author::isVedomstvo)
                .map(bool -> bool ? "true" : "false")
                .orElse("false");
    }

    @RequestMapping(method = POST, value = "/release")
    public ResponseEntity<String>  release(@RequestBody Map<String,String> allRequestParams){
        JsonObject jsonObject = new JsonObject();
        if (allRequestParams.get("commentId") != null) {
            postService.release(Long.valueOf(allRequestParams.get("commentId")));
        }
        return new ResponseEntity<>(new Gson().toJson(jsonObject),HttpStatus.OK);
    }


    @RequestMapping(method = GET, value = "/vkload")
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return offset;
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

    @RequestMapping(method = POST, value = "/send")
    public String upload(@RequestBody Image img) throws IOException {
        imageSaveService.save(img);
        return "index";
    }

    @RequestMapping(method = GET, value = "/rectags")
    public @ResponseBody List<Tag> recordTags(){
        return tagRepository.findAll();
    }


}
