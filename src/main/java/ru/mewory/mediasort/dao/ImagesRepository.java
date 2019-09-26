package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.Image;


public interface ImagesRepository extends JpaRepository<Image,Long> {
}
