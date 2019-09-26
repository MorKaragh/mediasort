package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.Image;

/**
 * Created by tookuk on 9/18/17.
 */
public interface ImagesRepository extends JpaRepository<Image,Long> {
}
