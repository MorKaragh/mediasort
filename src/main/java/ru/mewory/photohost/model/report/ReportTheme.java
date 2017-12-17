package ru.mewory.photohost.model.report;

import java.util.List;

/**
 * Created by tookuk on 11/23/17.
 */
public class ReportTheme {
    private List<ReportElement> elements;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ReportElement> getElements() {
        return elements;
    }

    public void setElements(List<ReportElement> elements) {
        this.elements = elements;
    }
}
