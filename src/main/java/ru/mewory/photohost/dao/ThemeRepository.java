package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.photohost.model.Location;
import ru.mewory.photohost.model.Theme;

/**
 * Created by tookuk on 10/10/17.
 */
public interface ThemeRepository extends JpaRepository<Theme,String> {
    Theme findByName(String name);
}

