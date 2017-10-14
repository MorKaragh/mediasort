package ru.mewory.photohost.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.photohost.dao.AuthorRepository;
import ru.mewory.photohost.dao.LocationRepository;
import ru.mewory.photohost.dao.TagRepository;
import ru.mewory.photohost.dao.ThemeRepository;
import ru.mewory.photohost.model.*;
import ru.mewory.photohost.service.ImageSaveService;
import ru.mewory.photohost.service.RecordSaveService;
import ru.mewory.photohost.service.SimpleReportService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping("/photo")
    public ModelAndView photo(Model model) {
        ModelAndView mav = new ModelAndView("photo");
        List<Location> locations = locationRepository.findAll();
        List<Author> authors = authorRepository.findAll();
        mav.addObject("locations", locations);
        mav.addObject("authors", authors);
        return mav;
    }

    @RequestMapping(value = { "/", "/record" })
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

    @RequestMapping(method = RequestMethod.POST, value = "/send")
    public String upload(@RequestBody Image img) throws IOException {
        imageSaveService.save(img);
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/sendRecord")
    public ResponseEntity<Map<String,String>> sendRecord(@RequestBody Record record) throws IOException {
        recordSaveService.save(record);
        Map<String,String> result = new HashMap<>();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method=RequestMethod.GET, value="rectags")
    public @ResponseBody List<Tag> recordTags(){
        return tagRepository.findAll();
    }

    @RequestMapping(method=RequestMethod.GET, value="report")
    public @ResponseBody ModelAndView getReport(){
        ModelAndView mav = new ModelAndView("report");
        List<Record> records = reportService.getReport(null);
        mav.addObject("report",records);
        List<Location> locations = locationRepository.findAll();
        mav.addObject("locations", locations);
        List<Theme> themes = themeRepository.findAll();
        mav.addObject("themes", themes);
        return mav;
    }

}
