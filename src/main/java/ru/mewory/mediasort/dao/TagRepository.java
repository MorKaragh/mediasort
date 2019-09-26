package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.Tag;

/**
 * Created by tookuk on 9/4/17.
 */
public interface TagRepository extends JpaRepository<Tag,Long>{
    Tag findByName (String name);
}
