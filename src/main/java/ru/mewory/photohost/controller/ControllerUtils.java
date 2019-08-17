package ru.mewory.photohost.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.photohost.dao.LocationRepository;
import ru.mewory.photohost.dao.ThemeRepository;
import ru.mewory.photohost.model.dictionaries.Location;
import ru.mewory.photohost.model.dictionaries.Theme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
public class ControllerUtils {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ThemeRepository themeRepository;

    public Date parseDate(String date) {
        if (StringUtils.isNotBlank(date)) {
            try {
                return SIMPLE_DATE_FORMAT.parse(date);
            } catch (ParseException e) {
                return null;
            }
        } return null;
    }

    public void fillDictionaries(ModelAndView mav) {
        List<Location> locations = locationRepository.findAll();
        locations.sort(Comparator.comparing(Location::getName));
        mav.addObject("locations", locations);
        List<Theme> themes = themeRepository.findAll();
        themes.sort(Comparator.comparing(Theme::getName));
        mav.addObject("themes", themes);
    }



}
