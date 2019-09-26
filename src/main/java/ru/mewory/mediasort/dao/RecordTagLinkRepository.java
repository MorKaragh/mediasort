package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.RecordTagLink;

import java.util.List;

/**
 * Created by tookuk on 10/8/17.
 */
public interface RecordTagLinkRepository  extends JpaRepository<RecordTagLink,Long> {
    List<RecordTagLink> findByRecordId(Long recordId);
}
