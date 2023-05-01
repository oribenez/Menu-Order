package com.example.menuapp;

/**
 * A class that represents a menu item in the order list
 */
public class OrderItem {
    private MenuItem mi;
    private int quantity;

    /**
//     * Constructor to create an instance of an OrderItem
     */
    public OrderItem(MenuItem mi, int quantity){
        this.mi = mi;
        this.quantity = quantity;
    }

    /**
     * Method which calculates the total price of this item multiplied by its quantity
     * @return total price
     */
    public double getTotal(){
        return this.mi.getPrice() * this.quantity;
    }

    //getter: quantity
    public int getQuantity(){
        return this.quantity;
    }
    //getter: MenuItem
    public MenuItem getMenuItem(){
        return this.mi;
    }
}
