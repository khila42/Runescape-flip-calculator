import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;

import org.json.*;

class PriceChecker {

    public static String[] items;
    public static Scanner kbd = new Scanner(System.in);
    public static ArrayList<Item> arrlist = new ArrayList<Item>();
    public static String lastItem = "";

    public static void main(String[] args) throws Exception {

        BufferedWriter bw = null;
        FileWriter fw = null;
        while (true) {
            switch (menu()) {
                case 'e':
                    excludeItem();
                    break;
                case 'm':
                String url = "https://rsbuddy.com/exchange/summary.json";
                String json =
                        new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
                items = json.split("},");
                    findOptimalItem();
                    break;
                case 'i':
                    excludeItem(lastItem);
                    break;

                case 'q':
                    quit();
            }
        }
    }

    public static char menu() {
        //add items
        System.out.println("(E)xclude\n\n(M)aximise profits\n(I)gnore last Iten" +
                "\n(Q)uit");
        String y = kbd.next();
        kbd.nextLine();
        char x = y.charAt(0);
        return x;
    }

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

     public static void excludeItem() throws Exception{
         File excluded = new File("exclusions.txt");
         System.out.println("\nWhat item would you like to exlude?");
         String exclusionInput = kbd.nextLine();
         Scanner fileScan = new Scanner(excluded);
         FileWriter myWriter = new FileWriter("exclusions.txt", true);
         String next = "";
         myWriter.write(exclusionInput+ "\n");
         myWriter.close();

    }

    public static void excludeItem(String excludedItem) throws Exception{
        File excluded = new File("exclusions.txt");
        Scanner fileScan = new Scanner(excluded);
        FileWriter myWriter = new FileWriter("exclusions.txt", true);
        String next = "";
        myWriter.write(excludedItem + "\n");
        myWriter.close();

   }

    public static void findOptimalItem() throws Exception {
        int min = -1;
        int max = -1;
        int maxAmount = 0;
        int profit = 0;
        int index = -1;
        int buyQuantity = 0;
        System.out.print("what is your cash stack: ");
        int cash = kbd.nextInt();
        for (int i = 0; i < items.length; i++) {
            Scanner currentItem = new Scanner(items[i]);
            String next = currentItem.nextLine();
            String[] itemBeingLookedAt = next.split(",");

            //isolate item ID
            String[] idArray = itemBeingLookedAt[0].split(":");
            int ID = Integer.parseInt(idArray[2]);


            String[] nameArray = itemBeingLookedAt[1].split(":");
            String name = nameArray[1];
            name = name.substring(1, name.length()-1);

            String[] minArray = itemBeingLookedAt[6].split(":");
            min = Integer.parseInt(minArray[1]);

            String[] maxArray = itemBeingLookedAt[4].split(":");
            max = Integer.parseInt(maxArray[1]);


            String[] buyQuantityArray = itemBeingLookedAt[5].split(":");
            buyQuantity = Integer.parseInt(buyQuantityArray[1]);

            arrlist.add(new Item(name, min, max, ID, buyQuantity));

        System.out.println("Done item " + (i + 1) + " of " + items.length);
        // count++;
    }

    for (int i = 0; i < arrlist.size(); i++) {
        try {
            if (arrlist.get(i).getMaxAmount() > (cash / arrlist.get(i).getLow())) {
                arrlist.get(i).setAmount(cash / arrlist.get(i).getLow());
            }
        } catch(Exception e) {
            arrlist.get(i).setAmount(0);
        }
        if (arrlist.get(i).getLow() == 0) {
            arrlist.get(i).setLow(Integer.MAX_VALUE);
        }

        int temp = ((arrlist.get(i).getMaxAmount() * (arrlist.get(i).getHigh()))
                - (arrlist.get(i).getMaxAmount() * arrlist.get(i).getLow()));
        if (temp > profit && (arrlist.get(i).getHigh() != 0) &&
          (arrlist.get(i).getLow() != 0) && arrlist.get(i).getMaxAmount() > 0 && !(excluded(i))){
            profit = temp;
            index = i;
        }
        System.out.println("computed " + (i+1) + " of " + arrlist.size() + " prices" );
    }
    System.out.println("the max profit is " + profit + " buying " +
            arrlist.get(index).getMaxAmount() + " of " + arrlist.get(index).getName()
            + " at " + arrlist.get(index).getLow() + " and selling them at "
            + arrlist.get(index).getHigh());
    lastItem = arrlist.get(index).getName();

    arrlist.clear();
    }


    public static boolean excluded(int index) throws Exception {
        File excluded = new File("exclusions.txt");
        Scanner exclusionsScan = new Scanner(excluded);
        String next = "";
        try {
            next = exclusionsScan.nextLine();
        } catch(Exception e) {
            return false;
        }
        while(next != null){
            if (arrlist.get(index).getName().equalsIgnoreCase(next)) {
                return true;
            }
            try {
                next = exclusionsScan.nextLine();
            } catch(Exception e) {
                return false;
            }
        }
        return false;
    }

    public static void quit() {
        System.exit(0);
    }

}
