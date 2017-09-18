package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.photohost.model.Image;

/**
 * Created by tookuk on 9/18/17.
 */
public interface ImagesRepository  extends JpaRepository<Image,Long> {

    Image findById (Long id);

}
