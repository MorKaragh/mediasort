package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.Tag;

import java.util.Date;
import java.util.List;

/**
 * Created by tookuk on 10/8/17.
 */
public interface RecordRepository extends JpaRepository<Record,Long> {
    List<Record> findByLocation(String location);
    List<Record> findByTheme(String theme);
    List<Record> findByThemeAndLocationAndDateBetween(String theme, String location, Date startDate, Date endDate);
    List<Record> findByDateBetween(Date startDate, Date endDate);
    List<Record> findByThemeAndDateBetween(String theme, Date startDate, Date endDate);
    List<Record> findByLocationAndThemeAndDescriptionAndDateBetween(String location, String theme, String description, Date startDate, Date endDate);

    @Query("SELECT r " +
            " FROM Record r, Comment c " +
            " WHERE c.date BETWEEN ?4 AND ?5 " +
            " AND r.theme = ?2 " +
            " AND c.id = r.commentId " +
            " AND r.location = ?1 " +
            " AND r.description = ?3 " +
            " AND c.status NOT IN ('NO_PLACE','NO_THEME') ")
    List<Record> findForReport(String location, String theme, String description, Date startDate, Date endDate);

    @Query("SELECT count(1) AS cnt, " +
            "r.location, r.description, " +
            "count(case p.socnet when 'VK' then 1 else null end) AS vkcnt, " +
            "count(case p.socnet when 'INSTAGRAM' then 1 else null end) AS instacnt " +
            " FROM Record r, Comment c, Post p " +
            " WHERE c.date BETWEEN ?1 AND ?2 " +
            " AND r.theme = ?3 " +
            " AND c.id = r.commentId " +
            " AND c.post = p " +
            " AND c.status NOT IN ('NO_PLACE','NO_THEME') " +
            " GROUP BY r.location, r.description ")
    List<Object[]> getGroupedReport(Date start, Date end, String theme);

    Record findByCommentId(Long commentId);

    @Query("SELECT DISTINCT r.theme " +
            " FROM Record r, Comment c " +
            " WHERE c.date BETWEEN ?1 AND ?2 " +
            " AND c.id = r.commentId " +
            " GROUP BY r.theme, r.location, r.description ")
    List<String> getThemesByDates(Date startDate, Date endDate);
}
