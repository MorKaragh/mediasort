package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.dictionaries.DictsEditHistory;

import java.util.List;

public interface DictEditHistoryRepository extends JpaRepository<DictsEditHistory, Long> {
    List<DictsEditHistory> findByRecord(Record record);
}

