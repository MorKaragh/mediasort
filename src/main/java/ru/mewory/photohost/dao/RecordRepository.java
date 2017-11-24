package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
