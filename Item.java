import java.util.Scanner;
import java.net.*;
import java.io.*;

/**
    Class to create an Item object to supplement PriceChecker.java
    @author Nikki McIntyre
*/

class Item{
    //Instance variables
    private int low;
    private int high;
    private int maxAmount;
    private String name;
    private int ID;
    private int buyQuantity;

    /**
        Creates the object using the name, lowest price, highest price,
        item ID and how much you can buy
    */
    public Item(String name, int low, int high, int ID, int buyQuantity)
    throws Exception{
        this.name = name;
        this.ID = ID;
        this.low = low;
        this.high = high;
        /* if the game imposed maximum items you're allowed to buy is less than
          the amount you could buy given your money, set the amount equal to the
          game imposed limit */
        if (findMaxItems() < buyQuantity) {
            this.maxAmount = findMaxItems();
        }
        // otherwise set it to the max you can afford
        else {
            this.maxAmount = buyQuantity;
        }

    }

    /**
        Method to search through the game imposed buy limits of items
        to find how much the game allows you to
    */
    public int findMaxItems() throws Exception{
        // Open the file with the buy limits on it
        File tradeLimits
            = new File("item buy limits edited.txt");
        Scanner info = new Scanner(tradeLimits);
        // reads the next line in the file
        String next = info.nextLine();
        // while there still text in the file
        while (next!=null) {
            //if the line contains the name of the item being looked at
            if(next.contains(name)){
                // skip 1 line
                String line1 = info.nextLine();
                // take the information from the second which has the buy limit
                String limit = info.nextLine();
                // return that limit
                return Integer.parseInt(limit);
            }
            // try to go to the next line
            try {
                next = info.nextLine();
            }
            // if it's null then it must be the end of the file
            catch (Exception e) {
                next = null;
            }
        }
        // return -1 as an error
        return -1;
    }

    // Setters and getters

    /**
        @return name of object
    */
    public String getName(){
        return name;
    }

    /**
        @param newName name to change to
    */
    public void setName(String newName){
        name = newName;
    }

    /**
        @return lowest price of object
    */
    public int getLow(){
        return low;
    }

    /**
        @param newLow lowest price to change to
    */
    public void setLow(int newLow){
        low = newLow;
    }

    /**
        @return highest price of object
    */
    public int getHigh(){
        return high;
    }

    /**
        @param newHigh highest price to change to
    */
    public void setHigh(int newHigh){
        high = newHigh;
    }

    /**
        @return how much of the object you can buy
    */
    public int getMaxAmount(){
        return maxAmount;
    }

    /**
        @param newAmount amount you can buy to change to
    */
    public void setAmount(int newAmount){
        maxAmount = newAmount;
    }
}
