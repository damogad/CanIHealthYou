package com.example.canihealthyou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

enum MedicationType {
    PILL,
    EFFERVESCENT_TABLET,
    POWDER_PACKET;
}


public class MedicationItem implements Serializable {

    private String name;
    private String description;
    private MedicationType medicationType;
    private int quantity;
    private ArrayList<Calendar> schedule;
    private int photoId = -1;
    private String photoPath;

    public MedicationItem(String name, String description, MedicationType medicationType, int quantity, ArrayList<Calendar> schedule) {
        this.name = name;
        this.description = description;
        this.medicationType = medicationType;
        this.quantity = quantity;
        this.schedule = new ArrayList<Calendar>(schedule);
    }

    public MedicationItem(String name, String description, MedicationType medicationType, int quantity, ArrayList<Calendar> schedule, int photoId) {
        this(name, description, medicationType, quantity, schedule);
        this.photoId = photoId;
    }

    public MedicationItem(String name, String description, MedicationType medicationType, int quantity, ArrayList<Calendar> schedule, String photoPath) {
        this(name, description, medicationType, quantity, schedule);
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MedicationType getMedicationType() {
        return medicationType;
    }

    public int getQuantity() {
        return quantity;
    }

    public ArrayList<Calendar> getSchedule() {
        return schedule;
    }

    public int getPhotoId() {
        return photoId;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
