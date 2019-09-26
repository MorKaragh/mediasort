package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.dictionaries.Theme;

/**
 * Created by tookuk on 10/10/17.
 */
public interface ThemeRepository extends JpaRepository<Theme,String>{
    Theme findByName(String name);
}

