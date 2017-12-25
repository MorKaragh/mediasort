package ru.mewory.photohost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.RecordRepository;
import ru.mewory.photohost.dao.RecordTagLinkRepository;
import ru.mewory.photohost.dao.TagRepository;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.RecordTagLink;
import ru.mewory.photohost.model.Tag;
import ru.mewory.photohost.model.report.ReportElement;
import ru.mewory.photohost.model.report.ReportTheme;

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
public class ReportService {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private RecordTagLinkRepository tagLinkRepository;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");


    public List<ReportTheme> getReport(Map<String, String> param){
        List<ReportTheme> themes = new ArrayList<>();
        if (param != null && !param.isEmpty()){
            Date startDate;
            Date endDate;
            try {
                startDate = simpleDateFormat.parse(param.get("startDate"));
                endDate = simpleDateFormat.parse(param.get("endDate"));
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage(),e);
            }
            themes = loadGroups(startDate, endDate);
        }
        return themes;
    }

    public List<ReportTheme> loadGroups(Date startDate, Date endDate) {
        List<String> locations = recordRepository.getThemesByDates(startDate, endDate);
        List<ReportTheme> themes = new ArrayList<>();
        for (String location : locations) {
            ReportTheme theme = new ReportTheme();
            theme.setLocation(location);
            List<Object[]> groupedReport = recordRepository.getGroupedReport(startDate, endDate, location);
            List<ReportElement> elements = new ArrayList<>();
            for (Object[] o : groupedReport) {
                ReportElement element = new ReportElement();
                element.setCount((Long) o[0]);
                element.setLocation((String) o[1]);
                element.setDescription((String) o[2]);
                elements.add(element);
            }
            theme.setElements(elements);
            themes.add(theme);
        }
        return themes;
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
