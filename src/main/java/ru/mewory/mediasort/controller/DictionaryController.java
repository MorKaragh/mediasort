package ru.mewory.mediasort.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.mediasort.dao.DictEditRepository;
import ru.mewory.mediasort.service.DictEditService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class DictionaryController {

    @Autowired
    private ControllerUtils controllerUtils;
    @Autowired
    private DictEditService dictService;
    @Autowired
    private DictEditRepository dictEditRepository;


    @RequestMapping(method = GET, value = "dictionaries")
    public @ResponseBody
    ModelAndView dictionaries(@RequestParam Map<String, String> allRequestParams) {
        ModelAndView mav = new ModelAndView("dictionaries");
        controllerUtils.fillDictionaries(mav);
        mav.addObject("transactions", dictEditRepository.findAll(new Sort(Sort.Direction.DESC, "id")));
        return mav;
    }

    @RequestMapping(method = POST, value = "changeDicts")
    public ResponseEntity<Map<String, String>> changeDicts(@RequestBody Map<String, String> allRequestParams) throws IOException {
        if (StringUtils.isNotEmpty(allRequestParams.get("oldLocation")) && StringUtils.isNotEmpty(allRequestParams.get("newLocation"))) {
            dictService.changeDictionary(allRequestParams.get("oldLocation"), allRequestParams.get("newLocation"), "Место");
        }
        if (StringUtils.isNotEmpty(allRequestParams.get("oldTheme")) && StringUtils.isNotEmpty(allRequestParams.get("newTheme"))) {
            dictService.changeDictionary(allRequestParams.get("oldTheme"), allRequestParams.get("newTheme"), "Тема");
        }
        Map<String, String> result = new HashMap<>();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = POST, value = "rollbackDicts")
    public ResponseEntity<Map<String, String>> rollbackDicts(@RequestBody Map<String, String> allRequestParams) throws IOException {
        dictService.rollbackTransaction();
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
    }


}
