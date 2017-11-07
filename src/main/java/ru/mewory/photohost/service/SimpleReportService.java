package ru.mewory.photohost.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.RecordRepository;
import ru.mewory.photohost.dao.RecordTagLinkRepository;
import ru.mewory.photohost.dao.TagRepository;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.RecordTagLink;
import ru.mewory.photohost.model.Tag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public List<Record> getReport(Map<String, String> param){
        List<Record> all = new ArrayList<>();
        if (param != null && !param.isEmpty()){
            Date startDate;
            Date endDate;
            try {
                startDate = simpleDateFormat.parse(param.get("startDate"));
                endDate = simpleDateFormat.parse(param.get("endDate"));
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage(),e);
            }
            String theme = param.get("theme");
            String location = param.get("location");
            all = recordRepository.findByThemeAndLocationAndDateBetween(
                    theme,
                    location,
                    startDate,
                    endDate);
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
