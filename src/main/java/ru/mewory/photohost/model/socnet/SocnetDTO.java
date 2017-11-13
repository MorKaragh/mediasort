package ru.mewory.photohost.model.socnet;

/**
 * Created by tookuk on 11/13/17.
 */
public class SocnetDTO {
    private String name;
    private String text;

    public SocnetDTO(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
