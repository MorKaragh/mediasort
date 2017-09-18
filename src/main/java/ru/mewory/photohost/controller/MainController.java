package ru.mewory.photohost.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mewory.photohost.model.Image;
import ru.mewory.photohost.service.SaveService;

import java.io.IOException;

/**
 * Created by tookuk on 9/3/17.
 */
@Controller
@EnableAutoConfiguration
public class MainController {

    @Autowired
    private SaveService saveService;

    @RequestMapping("/")
    public String index(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/send")
    public String upload(@RequestBody Image img) throws IOException {
        saveService.save(img);
        System.out.println("FILE?");
        return "index";
    }

}
