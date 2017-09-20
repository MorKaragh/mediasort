package ru.mewory.photohost.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.photohost.dao.AuthorRepository;
import ru.mewory.photohost.dao.LocationRepository;
import ru.mewory.photohost.model.Author;
import ru.mewory.photohost.model.Image;
import ru.mewory.photohost.model.Location;
import ru.mewory.photohost.service.SaveService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tookuk on 9/3/17.
 */
@Controller
@EnableAutoConfiguration
public class MainController {

    @Autowired
    private SaveService saveService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        ModelAndView mav = new ModelAndView("index");
        List<Location> locations = locationRepository.findAll();
        List<Author> authors = authorRepository.findAll();
        mav.addObject("locations", locations);
        mav.addObject("authors", authors);
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/send")
    public String upload(@RequestBody Image img) throws IOException {
        saveService.save(img);
        System.out.println("FILE?");
        return "index";
    }

}
