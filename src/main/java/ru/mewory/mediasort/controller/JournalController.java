package ru.mewory.mediasort.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.mediasort.model.JournalElement;
import ru.mewory.mediasort.model.socnet.Post;
import ru.mewory.mediasort.service.JournalService;
import ru.mewory.mediasort.service.socnet.PostService;
import ru.mewory.mediasort.service.socnet.RefreshService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class JournalController {

    @Autowired
    private JournalService journalService;
    @Autowired
    private RefreshService refreshService;
    @Autowired
    private PostService postService;

    Logger logger = LoggerFactory.getLogger(JournalController.class);


    @RequestMapping(method = GET, value = {"journal", "/"})
    public @ResponseBody
    ModelAndView getJournal(@RequestParam Map<String, String> allRequestParams) {
        logger.info("loading journal");
        ModelAndView mav = new ModelAndView("journal");
        List<JournalElement> posts = journalService.getJournal();
        mav.addObject("posts", posts);
        return mav;
    }

    @RequestMapping(method = POST, value = {"/fefreshPost"})
    public ResponseEntity<JournalElement> fefreshPost(@RequestBody Map<String, String> allRequestParams) {
        Long postNetId = Optional.ofNullable(allRequestParams.get("postNetId"))
                .map(Long::parseLong)
                .orElse(null);

        try {
            Post refresh = refreshService.refresh(postNetId, allRequestParams.get("postNetLink"));
            JournalElement singleElement = journalService.getSingleElement(refresh);
            singleElement.getPost().setComments(null);
            return new ResponseEntity<>(singleElement, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(method = POST, value = {"/deletePost"})
    public ResponseEntity<String> deletePost(@RequestBody Map<String, String> allRequestParams) {
        postService.deletePost(Long.valueOf(allRequestParams.get("postId")));
        return ResponseEntity.ok("удалено");
    }

}
