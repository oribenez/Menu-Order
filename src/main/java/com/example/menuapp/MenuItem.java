package com.example.menuapp;

/**
 * A class that represents an item in the menu
 */
public class MenuItem {
    public static final char CURRENCY_SYMBOL = '₪';
    private String name; // name of the item
    private String category; // category of the item
    private double price; // price of the item

    /**
     * Constructor that creates an instance oof MenuItem
     */
    public MenuItem(String name, String category, double price){
        this.name = name;
        this.category = category;
        this.price = price;
    }

    //getters & setters
    public String getName(){
        return this.name;
    }
    public double getPrice(){
        return this.price;
    }
    public String getPriceWCurrency(){
        return CURRENCY_SYMBOL + "" + this.price;
    }
    public String getCategory(){
        return this.category;
    }

    /**
     * Method which represents the menu item as a string and return it
     */
    @Override
    public String toString() {
        return "Item: " + this.name + ", category: " + this.category + ", price: " + CURRENCY_SYMBOL + this.price;
    }
}
