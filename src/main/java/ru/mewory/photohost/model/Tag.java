package ru.mewory.photohost.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by tookuk on 9/3/17.
 */
@Entity
@Table(name="tag")
public class Tag  implements Serializable {

    private Long id;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
