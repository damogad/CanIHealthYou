package com.example.canihealthyou;

import java.io.Serializable;

public class CalendarItem implements Serializable {
    private String title;
    private String description;

    public CalendarItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
