package com.example.canihealthyou;

import java.io.Serializable;

public class StepItem implements Serializable {
    private String description;
    private int hours;
    private int minutes;
    private int seconds;

    public StepItem(String description, int hours, int minutes, int seconds) {
        this.description = description;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public String getDescription() {
        return description;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }
}
