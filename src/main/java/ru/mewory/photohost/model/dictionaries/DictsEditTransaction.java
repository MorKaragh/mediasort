package ru.mewory.photohost.model.dictionaries;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dict_edit_transactions")
public class DictsEditTransaction {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<DictsEditHistory> history = new ArrayList<>();


    private String prevDictName;
    private String newDictName;
    private String dictClassName;
    private boolean wasCreatedNewEntry;


    public Long getId() {
        return id;
    }

    public DictsEditTransaction setId(Long id) {
        this.id = id;
        return this;
    }

    public List<DictsEditHistory> getHistory() {
        return history;
    }

    public DictsEditTransaction setHistory(List<DictsEditHistory> history) {
        this.history = history;
        return this;
    }

    public String getPrevDictName() {
        return prevDictName;
    }

    public DictsEditTransaction setPrevDictName(String prevDictName) {
        this.prevDictName = prevDictName;
        return this;
    }

    public String getNewDictName() {
        return newDictName;
    }

    public DictsEditTransaction setNewDictName(String newDictName) {
        this.newDictName = newDictName;
        return this;
    }

    public String getDictClassName() {
        return dictClassName;
    }

    public DictsEditTransaction setDictClassName(String dictClassName) {
        this.dictClassName = dictClassName;
        return this;
    }

    public void setWasCreatedNewEntry(boolean wasCreatedNewEntry) {
        this.wasCreatedNewEntry = wasCreatedNewEntry;
    }

    public boolean getWasCreatedNewEntry() {
        return wasCreatedNewEntry;
    }
}
