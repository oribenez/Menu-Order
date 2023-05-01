package com.example.menuapp;

import java.util.ArrayList;

/**
 * A class that represents an order that a client made
 */
public class Order {
    private ArrayList<OrderItem> listDishes; // list of all order items
    private String client; // the name of the client followed by his id

    /**
     * Constructor to create an instance of an order
     */
    public Order(){
        this.listDishes = new ArrayList<>();
        this.client = "";
    }

    /**
     * Method that adds an order item to the order
      * @param oi the order item to add to list
     */
    public void addDish(OrderItem oi){
        this.listDishes.add(oi);
    }

    /**
     * Method which removes an order item by its item name
     * @param itemName the name of the order item to remove from order list
     */
    public void removeDishByName(String itemName){
        for(OrderItem oi : this.listDishes){
            if(oi.getMenuItem().getName().equals(itemName)) {
                this.listDishes.remove(oi);
                return;
            }
        }
    }
    //getter: returns a list of all OrderItems
    public ArrayList<OrderItem> getListDishes(){
        return this.listDishes;
    }

    //setter: sets the name of the client for this order
    public void setClient(String client){
        this.client = client;
    }

    /**
     * Method which calculates the total amount of money to pay for this order
     * @return returns total price of all this order
     */
    public double getTotal(){
        double grandTotal = 0;
        for (OrderItem oi :this.listDishes){
            double itemTotal = oi.getTotal();
            grandTotal += itemTotal;
        }
        return grandTotal;
    }

    /**
     * Method which resets the order items list
     */
    public void reset(){
        this.listDishes.clear();
    }

    public boolean isEmpty(){
        return this.listDishes.size() == 0;
    }


    /**
     * Method which returns a string which represents this order, the string is in JSON format.
     * I have made it as a JSON format, so it will be easier later to get info from it and read the file.
     */

    /* example:*/
    // "client": Ori208333444,
    // "items":[
    // { name: "", price: , quantity: , total: }
    // ],
    // "grandTotal": ,
    @Override
    public String toString() {
        String str = "\"client\": " + this.client + ",\n\"items\":[\n";
        for(OrderItem oi : this.listDishes){
            str += "{\"name\":" + oi.getMenuItem().getName() + ",\"price\": " + oi.getMenuItem().getPrice() + ",\"quantity\": " + oi.getQuantity() + ",\"total\": " + oi.getTotal() + "},\n";
        }
        str += "],\"grandTotal\": " + this.getTotal();
        return str;
    }
}
