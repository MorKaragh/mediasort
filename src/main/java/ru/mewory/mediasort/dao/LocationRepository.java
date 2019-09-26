package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.dictionaries.Location;


public interface LocationRepository extends JpaRepository<Location,String>{
    Location findByName(String name);
}
