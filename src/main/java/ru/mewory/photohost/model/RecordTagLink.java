package ru.mewory.photohost.model;

import javax.persistence.*;

/**
 * Created by tookuk on 10/8/17.
 */
@Entity
@Table(name="recordTagLink")
public class RecordTagLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long recordId;
    private Long tagId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
