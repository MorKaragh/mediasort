package ru.mewory.mediasort.model.socnet;

import java.util.ArrayList;

public class InstagramLoaderObject {

    Owner OwnerObject;
    ArrayList<InstagramLoaderObject> answers = new ArrayList<>();
    private long id;
    private long created_at;
    private String text;

    public Owner getOwnerObject() {
        return OwnerObject;
    }

    public void setOwnerObject(Owner ownerObject) {
        OwnerObject = ownerObject;
    }

    public ArrayList<InstagramLoaderObject> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<InstagramLoaderObject> answers) {
        this.answers = answers;
    }

    // Getter Methods

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    // Setter Methods

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Owner getOwner() {
        return OwnerObject;
    }

    public void setOwner(Owner ownerObject) {
        this.OwnerObject = ownerObject;
    }
}