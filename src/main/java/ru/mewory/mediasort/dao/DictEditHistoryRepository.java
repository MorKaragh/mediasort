package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.Record;
import ru.mewory.mediasort.model.dictionaries.DictsEditHistory;

import java.util.List;

public interface DictEditHistoryRepository extends JpaRepository<DictsEditHistory, Long> {
    List<DictsEditHistory> findByRecord(Record record);
}

