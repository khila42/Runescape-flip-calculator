import java.util.Scanner;
import java.net.*;
import java.io.*;

class Item{
    private int low;
    private int high;
    private int maxAmount;
    private String name;
    private int ID;
    private int buyQuantity;
    public Item(String name, int low, int high, int ID, int buyQuantity)
    throws Exception{
        this.name = name;
        this.ID = ID;
        this.low = low;
        this.high = high;
        // System.out.println("The name is " + name);
        if (findMaxItems() < buyQuantity) {
            this.maxAmount = findMaxItems();
        }
        else {
            this.maxAmount = buyQuantity;
        }

    }

    public int findMaxItems() throws Exception{
        File tradeLimits
            = new File("item buy limits edited.txt");
        Scanner info = new Scanner(tradeLimits);
        String next = info.nextLine();
        while (next!=null) {
            if(next.contains(name)){
                String line1 = info.nextLine();
                String price = info.nextLine();
                return Integer.parseInt(price);
            }
            try {
                next = info.nextLine();
            }
            catch (Exception e) {
                next = null;
            }
        }
        // String url = "https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + id;
        // String json =
        //   new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
        // String[] data = json.split(",");
        // return Integer.parseInt(data[2].replaceAll("[\\D]", ""));
        return -1;
    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        name = newName;
    }

    public int getLow(){
        return low;
    }

    public void setLow(int newLow){
        low = newLow;
    }

    public int getHigh(){
        return high;
    }

    public void setHigh(int newHigh){
        high = newHigh;
    }

    public int getMaxAmount(){
        return maxAmount;
    }

    public void setAmount(int newAmount){
        maxAmount = newAmount;
    }
}
