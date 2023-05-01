package com.example.menuapp;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class which represents a menu in a restaurant
 */
public class Menu {
    private ArrayList<MenuItem> listMenu; // list of all items in menu

    /**
     * Constructor to create an instance of a menu object
     */
    public Menu(){
        listMenu = new ArrayList();
    }

    /**
     * Method which returns a list of MenuItems which are from a specific given category
     */
    public ArrayList<MenuItem> getListMenuByCategory(String category){
        ArrayList<MenuItem> tempList = new ArrayList<>();

        for (MenuItem mi : this.listMenu) {
            if(mi.getCategory().equals(category)) tempList.add(mi);
        }
        return tempList;
    }

    /**
     * Method which returns an array list of all categories which exists in a menu
     */
    public ArrayList<String> getAllCategories(){
        ArrayList<String> catList = new ArrayList<>();

        for (MenuItem mi : this.listMenu) {
            String currCat = mi.getCategory();
            boolean isCatExist = false;
            for (String miCat : catList) {
                if(miCat.equals(currCat)) {
                    isCatExist = true;
                    break;
                }
            }

            if(!isCatExist) catList.add(currCat);
        }
        return catList;
    }

    /**
     * Method which returns a MenuItem by its Name as an id
     */
    public MenuItem getMenuItemByName(String itemName){
        for (MenuItem mi : this.listMenu) {
            if(mi.getName().equals(itemName)) return mi;
        }
        return null;
    }

    /**
     * Method which reads a menu from a list of tokens as follows:
     * every three consecutive tokens represents:
     * 1. The name of the item in the menu
     * 2. The category which it belongs to
     * 3. the price of this item in the menu
     * @return true if the given list of tokens has been read successfully to this menu reference
     */
    public void readMenu(ArrayList tokens) throws InvalidInputFileException{
        if (tokens.size() % 3 != 0) throw new InvalidInputFileException();

        try {
            for (int i = 0; i < tokens.size(); i += 3) {
                String itemName = tokens.get(i).toString();
                String category = tokens.get(i + 1).toString();
                double price = Double.parseDouble(tokens.get(i + 2).toString());

                MenuItem mi = new MenuItem(itemName, category, price);
                listMenu.add(mi);
            }
        }catch (NumberFormatException e){
            throw new InvalidInputFileException();
        }
    }

    /**
     * Method which reads a file which represents a menu
     * @param filename the name of the file, as a default it is "menu.txt"
     * @return the menu as a list which every node represents a line in the file
     */
    public void readMenuFromFile(String filename) throws FileNotFoundException, InvalidInputFileException {
        try(Scanner input = new Scanner(new File(filename))) {
            ArrayList tokens = new ArrayList();

            while (input.hasNextLine()) {
                tokens.add(input.nextLine());
            }
            readMenu(tokens);

            // it is called try-with-resources, which ensures that the 'Scanner' will be
            // closed automatically when the try block is exited, even if an exception is thrown.
            // input.close(); // no need for that
        }
    }

    /**
     * @return string that represents this menu
     */
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < this.listMenu.size(); i++) {
            str += this.listMenu.get(i).toString() + "\n";
        }
        return str;
    }
}
