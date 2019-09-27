package ru.mewory.mediasort.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.mediasort.dao.TagRepository;
import ru.mewory.mediasort.exception.AllreadyHeldException;
import ru.mewory.mediasort.model.Author;
import ru.mewory.mediasort.model.Record;
import ru.mewory.mediasort.model.Tag;
import ru.mewory.mediasort.model.socnet.Comment;
import ru.mewory.mediasort.model.socnet.Post;
import ru.mewory.mediasort.service.RecordService;
import ru.mewory.mediasort.service.socnet.PostService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@EnableAutoConfiguration
public class PostEditController {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private RecordService recordService;
    @Autowired
    private PostService postService;
    @Autowired
    private ControllerUtils controllerUtils;


    @RequestMapping(value = {"/record"})
    public ModelAndView record(@RequestParam Map<String, String> allRequestParams) {
        ModelAndView mav = new ModelAndView("record");
        controllerUtils.fillDictionaries(mav);

        Post post;
        if (allRequestParams.get("strict") != null) {
            post = postService.getPostById(Long.valueOf(allRequestParams.get("postId")));
        } else {
            post = postService.getClosestPostToEdit(allRequestParams.get("postId"));
        }

        mav.addObject("realpost", "true");
        if (post == null) {
            mav.setViewName("journal");
        } else {
            mav.addObject("post", post);
        }
        return mav;
    }

    @RequestMapping(method = POST, value = "sendRecord")
    public ResponseEntity<Map<String, String>> sendRecord(@RequestBody Record record) throws IOException {
        recordService.save(record);
        Map<String, String> result = new HashMap<>();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = POST, value = "/setStatus")
    public ResponseEntity<String> setStatus(@RequestBody Map<String, String> allRequestParams) {
        JsonObject jsonObject = new JsonObject();
        postService.setTrashStatus(Long.valueOf(allRequestParams.get("commentId")), allRequestParams.get("status"));
        return new ResponseEntity<>(new Gson().toJson(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(method = POST, value = "/takeToWork")
    public ResponseEntity<String> takeToWork(@RequestBody Map<String, String> allRequestParams) {
        JsonObject jsonObject = new JsonObject();
        try {
            if (allRequestParams.get("commentId") != null) {
                Comment comment = postService.takeAndHold(Long.valueOf(allRequestParams.get("commentId")));
                jsonObject.addProperty("available", "true");
                jsonObject.addProperty("isVedomstvo", comment != null ? extractVedomstvoFlag(comment) : "false");
            } else {
                jsonObject.addProperty("error", "нет такого комментария");
                jsonObject.addProperty("available", "false");
            }
        } catch (AllreadyHeldException e) {
            jsonObject.addProperty("error", "этот комментарий уже обработан");
            jsonObject.addProperty("available", "false");
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
                .map(this::extractVedomstvoFlag)
                .orElse("false");
    }

    private String extractVedomstvoFlag(Comment e) {
        return Optional.ofNullable(e)
                .map(Comment::getAuthor)
                .map(Author::isVedomstvo)
                .map(bool -> bool ? "true" : "false")
                .orElse("false");
    }

    @RequestMapping(method = POST, value = "/release")
    public ResponseEntity<String> release(@RequestBody Map<String, String> allRequestParams) {
        JsonObject jsonObject = new JsonObject();
        if (allRequestParams.get("commentId") != null) {
            postService.release(Long.valueOf(allRequestParams.get("commentId")));
        }
        return new ResponseEntity<>(new Gson().toJson(jsonObject), HttpStatus.OK);
    }

    @RequestMapping(value = {"/", "/login"})
    public String login(Model model) {
        return "login";
    }

    @RequestMapping(method = GET, value = "/rectags")
    public @ResponseBody
    List<Tag> recordTags() {
        return tagRepository.findAll();
    }


}
