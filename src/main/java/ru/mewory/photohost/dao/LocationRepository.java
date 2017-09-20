package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.photohost.model.Author;
import ru.mewory.photohost.model.Location;

/**
 * Created by tookuk on 9/20/17.
 */
public interface LocationRepository extends JpaRepository<Location,String> {
    Location findByName(String name);
}
