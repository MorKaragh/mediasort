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
    List<Record> findByLocationAndDateBetween(String location, Date startDate, Date endDate);

    @Query("SELECT count(1) AS cnt, r.location, r.description " +
            " FROM Record r " +
            " WHERE r.date BETWEEN ?1 AND ?2 " +
            " AND r.theme = ?3 " +
            " GROUP BY r.location, r.description ")
    List<Object[]> getGroupedReport(Date start, Date end, String theme);

    Record findByCommentId(Long commentId);

    @Query("SELECT DISTINCT r.theme " +
            " FROM Record r " +
            " WHERE r.date BETWEEN ?1 AND ?2 " +
            " GROUP BY r.theme, r.location, r.description ")
    List<String> getThemesByDates(Date startDate, Date endDate);
}
