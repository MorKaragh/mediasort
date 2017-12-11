package ru.mewory.photohost.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.*;
import ru.mewory.photohost.model.*;
import ru.mewory.photohost.model.socnet.Comment;
import ru.mewory.photohost.model.socnet.CommentStatus;
import ru.mewory.photohost.utils.UserUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by tookuk on 10/1/17.
 */
@Service
public class RecordService {

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
    @Autowired
    private CommentsRepository commentsRepository;

    public Record save(Record record){
        assert record.getCommentId() != null;

        Comment c = commentsRepository.findById(record.getCommentId());
        assert c != null;
        c.setStatus(CommentStatus.DONE);
        c.setChangeUser(UserUtils.getUsername());
        commentsRepository.save(c);

        record.setDate(new Date());
        Record saved = recordRepository.save(record);

        Location location = locationRepository.findByName(record.getLocation());
        if (location == null && StringUtils.isNotBlank(record.getLocation())) {
            locationRepository.save(new Location(record.getLocation()));
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

    public Record loadByCommentId(Long id) {
        Record result = recordRepository.findByCommentId(id);
        if (result != null){
            List<RecordTagLink> byRecordId = recordTagLinkRepository.findByRecordId(result.getId());
            for (RecordTagLink link : byRecordId) {
                result.getTags().add(tagRepository.findById(link.getTagId()).getName());
            }
        }
        return result;
    }
}
