package ru.mewory.photohost.model.report;

import java.util.List;

/**
 * Created by tookuk on 11/23/17.
 */
public class ReportTheme {
    private List<ReportElement> elements;
    private String location;
    private Long vkCnt = 0L;
    private Long instagramCnt = 0L;
    private Long cnt = 0L;

    public Long getVkCnt() {
        return vkCnt;
    }

    public void setVkCnt(Long vkCnt) {
        this.vkCnt = vkCnt;
    }

    public Long getInstagramCnt() {
        return instagramCnt;
    }

    public void setInstagramCnt(Long instagramCnt) {
        this.instagramCnt = instagramCnt;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }

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
