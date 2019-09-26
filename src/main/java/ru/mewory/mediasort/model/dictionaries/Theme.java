package ru.mewory.mediasort.model.dictionaries;

import javax.persistence.*;

/**
 * Created by tookuk on 10/10/17.
 */
@Entity
@Table(name="themes",
        uniqueConstraints = {@UniqueConstraint(columnNames={"name"})}
)
public class Theme{

    public static final String DESCRIPTION = "Тема";

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

    public Theme() {
    }

    public Theme(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Theme setName(String name) {
        this.name = name;
        return this;
    }
}
