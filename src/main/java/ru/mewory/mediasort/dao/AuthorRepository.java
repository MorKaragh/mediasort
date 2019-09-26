package ru.mewory.mediasort.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mewory.mediasort.model.Author;


public interface AuthorRepository extends JpaRepository<Author,String> {
    Author findByName(String name);
}
