package ru.mewory.photohost.model.dictionaries;

import ru.mewory.photohost.model.Record;

import javax.persistence.*;

@Entity
@Table(name = "dict_edits")
public class DictsEditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "transaction_id")
    private DictsEditTransaction transaction;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "record_id")
    private Record record;

    public DictsEditTransaction getTransaction() {
        return transaction;
    }

    public DictsEditHistory setTransaction(DictsEditTransaction transaction) {
        this.transaction = transaction;
        return this;
    }

    public Long getId() {
        return id;
    }

    public DictsEditHistory setId(Long id) {
        this.id = id;
        return this;
    }

    public Record getComment() {
        return record;
    }

    public DictsEditHistory setComment(Record comment) {
        this.record = comment;
        return this;
    }

}
