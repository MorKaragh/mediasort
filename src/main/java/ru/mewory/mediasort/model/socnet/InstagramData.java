package ru.mewory.mediasort.model.socnet;

import com.fasterxml.jackson.annotation.JsonProperty;


public class InstagramData {

    @JsonProperty("data")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
