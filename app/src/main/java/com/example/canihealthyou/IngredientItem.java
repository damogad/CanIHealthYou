package com.example.canihealthyou;

import java.io.Serializable;

enum QuantityType {
    GRAMS,
    UNITS,
    MILLILITERS;
}

public class IngredientItem implements Serializable {
    private String name;
    private int quantity;
    private QuantityType quantityType;

    public IngredientItem(String name, int quantity, QuantityType quantityType) {
        this.name = name;
        this.quantity = quantity;
        this.quantityType = quantityType;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public QuantityType getQuantityType() {
        return quantityType;
    }
}