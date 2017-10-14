package ru.mewory.photohost.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.*;
import ru.mewory.photohost.model.*;

import java.util.Date;

/**
 * Created by tookuk on 10/1/17.
 */
@Service
public class RecordSaveService {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private RecordTagLinkRepository recordTagLinkRepository;
    @Autowired
    private ThemeRepository themeRepository;

    public Record save(Record record){
        record.setDate(new Date());
        Record saved = recordRepository.save(record);

        Location location = locationRepository.findByName(record.getLocation());
        if (location == null && StringUtils.isNotBlank(record.getLocation())) {
            locationRepository.save(new Location(record.getLocation()));
        }

        Author author = authorRepository.findByName(record.getAuthor());
        if (author == null && StringUtils.isNotBlank(record.getAuthor())) {
            authorRepository.save(new Author(record.getAuthor()));
        }

        Theme theme = themeRepository.findByName(record.getTheme());
        if (theme == null && StringUtils.isNotBlank(record.getTheme()) ){
            themeRepository.save(new Theme(record.getTheme()));
        }

        for(String tag : record.getTags()){
            Tag t = tagRepository.findByName(tag);
            if(t == null){
                t = new Tag();
                t.setName(tag);
                tagRepository.save(t);
            }
            RecordTagLink link = new RecordTagLink();
            link.setTagId(t.getId());
            link.setRecordId(saved.getId());
            recordTagLinkRepository.save(link);
        }

        return record;
    }

}
