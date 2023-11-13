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
    
    public boolean addResource(int resourceID, int quantity, String username) {
        if (quantity > 0){
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1){
                int new_quantity = userResources.get(resourceIndex).getQuantity() + quantity;
                userResources.get(resourceIndex).setQuantity(new_quantity);
                return true;
            }
        }
        return false;
    }
    
    public boolean removeResource(int resourceID, int quantity, String username) {
        if (quantity > 0){
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1){
                int new_quantity = userResources.get(resourceIndex).getQuantity() - quantity;
                userResources.get(resourceIndex).setQuantity(new_quantity);
                return true;
            }
        }
        return false;
    }

}
