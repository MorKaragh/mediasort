package ru.mewory.photohost.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.photohost.model.Author;

/**
 * Created by tookuk on 9/20/17.
 */
public interface AuthorRepository extends JpaRepository<Author,String> {
    Author findByName(String name);
}
