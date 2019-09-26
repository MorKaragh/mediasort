package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.Tag;


public interface TagRepository extends JpaRepository<Tag,Long>{
    Tag findByName (String name);
}
