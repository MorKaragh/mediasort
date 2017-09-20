package ru.mewory.photohost.model;

import javax.persistence.*;

/**
 * Created by tookuk on 9/20/17.
 */
@Entity
@Table(name="authors",
    uniqueConstraints = {@UniqueConstraint(columnNames={"name"})}
)
public class Author {

    private String name;
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
