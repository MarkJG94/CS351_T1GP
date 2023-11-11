import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

//get online users
public class User {

    ArrayList<Resource> userResources;
    String username;
    int funds;

    public User(String username, ArrayList<Resource> userResources){
        this.username = username.toLowerCase(Locale.ROOT);
        this.userResources = userResources;
        funds = 1000;
    }

    public User(String username, ArrayList<Resource> userResources, int funds){
        this.username = username;
        this.userResources = userResources;
        this.funds = funds;
    }
    
    public ArrayList<Resource> getUserInventory() {
        return userResources;

    }
    
    public int getResourceIndex(int resourceID) {
        for (Resource resource : userResources) {
            if (resource.getId() == resourceID) {
                return userResources.indexOf(resource);
            }
        }
        return -1;
    }

    
    public int getResourceQuantity(int resourceID) {
        return userResources.get(getResourceIndex( resourceID )).getQuantity();
    }

    
    public int addFunds(String username, int amount) {
        
        if (amount > 0 && username.equals( this.username ))
        {
            funds = funds + amount;
            return funds;
        }
        return -1;
    }
    

    
    public int deductFunds(String username, int amount) {
        if (amount > 0 && username.equals( this.username ) && validateCurrency( amount ))
        {
            funds = funds - amount;
            return funds;
        }
        return -1;
    }


    
    public boolean validateCurrency(int amount) {
        if (funds >= amount){
            return true;
        }
        return false;
    }
    
    public int getFunds() {
        return funds;
    }

    public String getUsername(){
        return username;
    }
    
    public boolean addItem(String itemID, int quantity) {
        //read file, add to wood or iron ... quantity
        boolean added = false;
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                    if(itemID == "wood"){
                        wood = quantity+wood;
                        added=true;
                        return added;
                    }else if (itemID=="stone"){
                        stone = quantity+stone;
                        added=true;
                        return added;
                    }else if (itemID=="iron"){
                        stone = quantity+iron;
                        added=true;
                        return added;
                    }else if (itemID=="gold"){
                        stone = quantity+gold;
                        added=true;
                        return added;
                    }else if (itemID=="silver"){
                        stone = quantity+silver;
                        added=true;
                        return added;
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //doesnt write back to file yet
        return added;
    }

    
    public boolean removeItem(String itemID, int quantity) {
        //read file, add to wood or iron ... quantity
        boolean added = false;
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                    if(itemID == "wood"){
                        wood = quantity-wood;
                        added=true;
                        return added;
                    }else if (itemID=="stone"){
                        stone = quantity-stone;
                        added=true;
                        return added;
                    }else if (itemID=="iron"){
                        stone = quantity-iron;
                        added=true;
                        return added;
                    }else if (itemID=="gold"){
                        stone = quantity-gold;
                        added=true;
                        return added;
                    }else if (itemID=="silver"){
                        stone = quantity-silver;
                        added=true;
                        return added;
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //doesnt write back to file yet
        return added;
    }

    public boolean buyItem(ArrayList<Resource> userResources, int funds){
        return true;
        //username, resource, fund
    }

    public boolean sellItem(ArrayList<Resource> userResources, int funds){
        return true;
    }

    public boolean notifyUser(String username, int quantity, String resourceName){

        System.out.println("Your "+ resourceName + "is now" + quantity);
        return true;
    }



}
