import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;

import org.json.*;

/**
    This is a program for Old School Runescape (OSRS) to check the Grand exchange
    (other games may call it an auction house) and look at all the items being
    sold. It will then return the most profitable item to flip (buy low then
    sell high)
    Required files: PriceChecker.java, Item.java, item buy limits edited.txt
    @author Nikki McIntyre
*/
class PriceChecker {

    //Array to hold information for individual items
    public static String[] items;
    //keyboard scanner
    public static Scanner kbd = new Scanner(System.in);
    //Arraylist to hold information about the prices of items
    public static ArrayList<Item> arrlist = new ArrayList<Item>();
    //String that'll hold the last optimal item found
    public static String lastItem = "";


    public static void main(String[] args) throws Exception {
        //Read and write to files
        BufferedWriter bw = null;
        FileWriter fw = null;

        //Main loop for the program
        while (true) {
            switch (menu()) {
                //Add item to the exclusion list
                case 'e':
                    excludeItem();
                    break;
                //Maximise the profits
                case 'm':
                /*JSON file that contains a library of every item being traded
                Using a cannonball for example it looke like:

                "id":2,"name":"Cannonball","members":true,"sp":5,
                "buy_average":135,"buy_quantity":33100,"sell_average":134,
                "sell_quantity":23194,"overall_average":134,
                "overall_quantity":56294

                (all on one line, line breaks by me are for readability)
                And that format is the exact same for all the items
                */
                String url = "https://rsbuddy.com/exchange/summary.json";
                String json =
                        new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();

                /* makes an array of items each item having 1 slot in the Array
                 the entire item information seen in the previous comment
                 is all one slot*/

                items = json.split("},");
                //find the optimal item
                    findOptimalItem();
                    break;
                //Exclude the last optimal item
                case 'i':
                    excludeItem(lastItem);
                    break;
                //Quit the program
                case 'q':
                    quit();
            }
        }
    }

    /**
        shows the menu where the user can input a value for what option
        they wanna select
        @return The character of which the user selected
    */
    public static char menu() {
        System.out.println("(E)xclude\n(M)aximise profits\n(I)gnore last Iten" +
                "\n(Q)uit");
        String y = kbd.next();
        kbd.nextLine();
        char x = y.charAt(0);
        return x;
    }

    /**
        Removes an item from the exclusions document
        TODO make this actually work
    */
     public static void includeItem() throws Exception{
         File inputFile = new File("exclusions.txt");
         File tempFile = new File("Temp.txt");

         BufferedReader reader = new BufferedReader(new FileReader(inputFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

         System.out.println("\nWhat item would you like to include?");
         String lineToRemove = kbd.nextLine();
         String currentLine;

         while((currentLine = reader.readLine()) != null) {
             // trim newline when comparing with lineToRemove
             String trimmedLine = currentLine.trim();
             if(trimmedLine.equals(lineToRemove)) continue;
             writer.write(currentLine + System.getProperty("line.separator"));
         }
         writer.close();
         reader.close();
         boolean successful = tempFile.renameTo(inputFile);
    }

    /**
        Adds an item on the exclusions document so the program will skip
        over it
        @exception e the I/O exception required to read and write to And
        from a file
    */
     public static void excludeItem() throws Exception{
         //Opens up the exclusions document
         File excluded = new File("exclusions.txt");
         //prompt the user to find the item they wish to exclude
         System.out.println("\nWhat item would you like to exlude?");
         String exclusionInput = kbd.nextLine();
         //scanner for the exclusions document
         Scanner fileScan = new Scanner(excluded);
         //writer for the exclusions document
         FileWriter myWriter = new FileWriter("exclusions.txt", true);
         //write the excluded item in the exclusions document and make a newline
         myWriter.write(exclusionInput+ "\n");
         //close the writer
         myWriter.close();

    }

    /**
        Exclusion method used by ignore last item and will be passed
        the parameter of the last item found
        @param excludedItem The item to be ignored
    */
    public static void excludeItem(String excludedItem) throws Exception{
        File excluded = new File("exclusions.txt");
        Scanner fileScan = new Scanner(excluded);
        FileWriter myWriter = new FileWriter("exclusions.txt", true);
        String next = "";
        myWriter.write(excludedItem + "\n");
        myWriter.close();

   }

   /**
        The method that does the vast majority of the work in this file, it will
        sort through every item and find the one with the biggest gap between
        the buying price and selling price that you can afford with your cash
        stack and the amount of that item you can buying
        @throws e the I/O exception needed to read and write to files
   */
    public static void findOptimalItem() throws Exception {
        /*initialise variables to the least amount they can be to be adjusted
        as the method continues*/
        int min = -1;
        int max = -1;
        int maxAmount = 0;
        int profit = 0;
        int index = -1;
        int buyQuantity = 0;
        //Take user input for how much cash they got to buy the item
        System.out.print("what is your cash stack: ");
        int cash = kbd.nextInt();

        /*Loop through every item in the items array initialised in main to
        split the information into a more usable state and make it an items
        (see Item.java)
        */
        for (int i = 0; i < items.length; i++) {
            //Scanner to read the information in the item currently being looked at
            Scanner currentItem = new Scanner(items[i]);
            /*splits the item currently being looked at into an arrau
                formatted so each field gets it's own slot in the array
                I.E. looking at the JSOM "id", "name" "sell average" etc call
                get 1 slot
            */
            String next = currentItem.nextLine();
            String[] itemBeingLookedAt = next.split(",");

            //isolate item ID
            String[] idArray = itemBeingLookedAt[0].split(":");
            int ID = Integer.parseInt(idArray[2]);

            //isolate the name
            String[] nameArray = itemBeingLookedAt[1].split(":");
            String name = nameArray[1];
            name = name.substring(1, name.length()-1);

            //isolate the lowest sell average
            String[] minArray = itemBeingLookedAt[6].split(":");
            min = Integer.parseInt(minArray[1]);

            //isolate the highest buy average
            String[] maxArray = itemBeingLookedAt[4].split(":");
            max = Integer.parseInt(maxArray[1]);

            //isolate the quantity of that particular items being sold
            String[] buyQuantityArray = itemBeingLookedAt[5].split(":");
            buyQuantity = Integer.parseInt(buyQuantityArray[1]);

            arrlist.add(new Item(name, min, max, ID, buyQuantity));
        //print out the progress so user knows how many items have been added
        System.out.println("Done item " + (i + 1) + " of " + items.length);
    }

    /* for loop that will go through every item that was put into the ArrayList
      in the previous code block */
    for (int i = 0; i < arrlist.size(); i++) {
        /*if the amount thats being traded is more than you can buy setLow
        the amount you can buy to cash/the low price */
        try {
            if (arrlist.get(i).getMaxAmount() > (cash / arrlist.get(i).getLow())) {
                arrlist.get(i).setAmount(cash / arrlist.get(i).getLow());
            }
        }
        //if theres an exception just set the amount you can buy to 0
        catch(Exception e) {
            arrlist.get(i).setAmount(0);
        }
        /* if the low is 0 which would cause weird results but happens
        with infrequently traded items, set the low to the max int value to
        effectively ignore it */
        if (arrlist.get(i).getLow() == 0) {
            arrlist.get(i).setLow(Integer.MAX_VALUE);
        }

        /* maths out the profit of the item. ((amount you can buy)*(highest price))
        -((amount you can buy) * (lowest price))
        and stores that profit into an int */
        int temp = ((arrlist.get(i).getMaxAmount() * (arrlist.get(i).getHigh()))
                - (arrlist.get(i).getMaxAmount() * arrlist.get(i).getLow()));

        /* If the temp int gotten from the last line is greater than the current
        greatest profit replace it unless the item is in the exclusion document
        and the low amount isn't bugged out as can happen with low volume
        traded items */
        if (temp > profit && (arrlist.get(i).getHigh() != 0) &&
          (arrlist.get(i).getLow() != 0) && arrlist.get(i).getMaxAmount() > 0 && !(excluded(i))){
            profit = temp;
            index = i;
        }
        //print out the progress of the prices being computed
        System.out.println("computed " + (i+1) + " of " + arrlist.size() + " prices" );
    }

    // Print out the results of all the computation
    try {
        System.out.println("the max profit is " + profit + " buying " +
                arrlist.get(index).getMaxAmount() + " of " + arrlist.get(index).getName()
                + " at " + arrlist.get(index).getLow() + " and selling them at "
                + arrlist.get(index).getHigh());
        lastItem = arrlist.get(index).getName();
    }
    /* there is a known error sometimes with the json where everything returns 0
     putting the index at -1 so print this out */
    catch(Exception e) {
        System.err.println("***Something went wrong when trying to find an item!***");
    }

    /* clear the arraylist so it can be repopulated when called again with more
    recent values */
    arrlist.clear();
    }

    /**
        Method to check the excluded document to see if an item is on it
        @param index the index of the arraylist the item being looked at is in
        @return a boolean on if the item is on the excluded list or not
    */
    public static boolean excluded(int index) throws Exception {
        //opens up the excluded document
        File excluded = new File("exclusions.txt");
        Scanner exclusionsScan = new Scanner(excluded);
        //String that'll hold the next value in the document
        String next = "";
        /* will call an exception if next is null therefore there are no items
        on the exclusions list */
        try {
            next = exclusionsScan.nextLine();
        } catch(Exception e) {
            return false;
        }
        // while there are still lines in the document
        while(next != null){
            /* if the name on the document matches the name of the index being
            looked at return true*/
            if (arrlist.get(index).getName().equalsIgnoreCase(next)) {
                return true;
            }
            // if reached the end of the document without a match return false
            try {
                next = exclusionsScan.nextLine();
            } catch(Exception e) {
                return false;
            }
        }
        // this false likely won't trigger however the compiler needs it
        return false;
    }

    /**
        exits the program with error code 0 (intentional close)
    */
    public static void quit() {
        System.exit(0);
    }

}
