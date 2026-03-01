package com.example.receipts.models;

public class Recipe {
    private int id;
    private String category;
    private String cuisine;
    private int nameResId;
    private int ingredientsResId;
    private int stepsResId;

    public Recipe(int id, String category, String cuisine, int nameResId, int ingredientsResId, int stepsResId) {
        this.id = id;
        this.category = category;
        this.cuisine = cuisine;
        this.nameResId = nameResId;
        this.ingredientsResId = ingredientsResId;
        this.stepsResId = stepsResId;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public String getCuisine() { return cuisine; }
    public int getNameResId() { return nameResId; }
    public int getIngredientsResId() { return ingredientsResId; }
    public int getStepsResId() { return stepsResId; }
}
