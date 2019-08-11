package ru.mewory.photohost.model.dictionaries;

import javax.persistence.*;

/**
 * Created by tookuk on 9/20/17.
 */
@Entity
@Table(name="locations",
    uniqueConstraints = {@UniqueConstraint(columnNames={"name"})}
)
public class Location{

    private String name;
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location() {
    }

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location setName(String name) {
        this.name = name;
        return this;
    }
}
