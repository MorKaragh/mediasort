package ru.mewory.photohost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.CommentsRepository;
import ru.mewory.photohost.dao.RecordRepository;
import ru.mewory.photohost.dao.RecordTagLinkRepository;
import ru.mewory.photohost.dao.TagRepository;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.RecordTagLink;
import ru.mewory.photohost.model.Tag;
import ru.mewory.photohost.model.report.ReportElement;
import ru.mewory.photohost.model.report.ReportTheme;

import java.text.ParseException;
import java.util.*;

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
    @Autowired
    private CommentsRepository commentsRepository;

    public int getVedomstvaCount(Date startDate, Date endDate){
        if (startDate == null || endDate == null) {
            return 0;
        }
        return recordRepository.countVedomstva(startDate, endDate);
    }

    public int getTotalVkCount(Date startDate, Date endDate){
        if (startDate == null || endDate == null) {
            return 0;
        }
        return commentsRepository.countVkComments(startDate, endDate);
    }

    public int getTotalInstagramCount(Date startDate, Date endDate){
        if (startDate == null || endDate == null) {
            return 0;
        }
        return commentsRepository.countInstagramComments(startDate, endDate);
    }

    public int getDistinctUsersCount(Date startDate, Date endDate){
        if (startDate == null || endDate == null) {
            return 0;
        }
        return recordRepository.countDistinctUsers(startDate, endDate);
    }

    public List<ReportTheme> getReport(Date startDate, Date endDate){
        List<ReportTheme> themes = loadGroups(startDate, endDate);
        themes.sort(Comparator.comparing(ReportTheme::getCnt).reversed());
        return themes;
    }

    private List<ReportTheme> loadGroups(Date startDate, Date endDate) {
        List<ReportTheme> themes = new ArrayList<>();
        if (startDate == null || endDate == null) {
            return themes;
        }
        List<String> locations = recordRepository.getThemesByDates(startDate, endDate);
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
                element.setVkCount((Long) o[3]);
                element.setInstagramCount((Long) o[4]);
                element.setUserCount((Long) o[6]);
                element.setAdditionalText((String) o[5]);
                elements.add(element);
                theme.setCnt(theme.getCnt() + element.getCount());
                theme.setVkCnt(theme.getVkCnt() + element.getVkCount());
                theme.setInstagramCnt(theme.getInstagramCnt() + element.getInstagramCount());
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
                    Tag tag = tagRepository.findById(link.getTagId()).get();
                    r.getTags().add(tag.getName());
                }
            }
        }
    }

}
