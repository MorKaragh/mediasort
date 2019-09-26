package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mewory.mediasort.model.dictionaries.DictsEditTransaction;

public interface DictEditRepository extends JpaRepository<DictsEditTransaction, Long> {

    @Query("select max(t.id) from DictsEditTransaction t")
    Long getMaxId();
}
