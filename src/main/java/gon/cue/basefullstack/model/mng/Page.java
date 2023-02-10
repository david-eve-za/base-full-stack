package gon.cue.basefullstack.model.mng;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;


public class Page {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
