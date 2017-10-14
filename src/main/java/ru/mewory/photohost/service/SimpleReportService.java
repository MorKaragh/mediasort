package ru.mewory.photohost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.RecordRepository;
import ru.mewory.photohost.dao.RecordTagLinkRepository;
import ru.mewory.photohost.dao.TagRepository;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.RecordTagLink;
import ru.mewory.photohost.model.Tag;

import java.util.List;

/**
 * Created by tookuk on 10/8/17.
 */
@Service
public class SimpleReportService {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private RecordTagLinkRepository tagLinkRepository;

    public List<Record> getReport(String location){
        List<Record> all;
        if (location != null){
            all = recordRepository.findByLocation(location);
        } else {
            all = recordRepository.findAll();
        }
        if (all != null){
            putTags(all);
        }
        return all;
    }

    private void putTags(List<Record> all) {
        for (Record r : all){
            List<RecordTagLink> byRecordId = tagLinkRepository.findByRecordId(r.getId());
            if (byRecordId != null){
                for (RecordTagLink link : byRecordId){
                    Tag tag = tagRepository.findById(link.getTagId());
                    r.getTags().add(tag.getName());
                }
            }
        }
    }

}
