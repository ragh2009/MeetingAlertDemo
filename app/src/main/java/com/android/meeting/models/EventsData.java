package com.android.meeting.models;

import java.io.Serializable;

public class EventsData implements Serializable {
    private int id;
    private String title;
    private String description;
    private String dtStart;
    private String dtEnd;
    private String eventLocation;
    private String eTimeFormat;

    public String geteTimeFormat() {
        return eTimeFormat;
    }

    public void seteTimeFormat(String eTimeFormat) {
        this.eTimeFormat = eTimeFormat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDtStart() {
        return dtStart;
    }

    public void setDtStart(String dtStart) {
        this.dtStart = dtStart;
    }

    public String getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(String dtEnd) {
        this.dtEnd = dtEnd;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
}
