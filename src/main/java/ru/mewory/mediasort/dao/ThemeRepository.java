package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.dictionaries.Theme;


public interface ThemeRepository extends JpaRepository<Theme,String>{
    Theme findByName(String name);
}

