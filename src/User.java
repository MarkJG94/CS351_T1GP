import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

//get online users
public class User {

    ArrayList<Resource> userResources;
    String username;
    String password;
    boolean status;
    int funds;

    public User(String username, String password, ArrayList<Resource> userResources){
        this.username = username.toLowerCase(Locale.ROOT);
        this.password = password;
        this.userResources = userResources;
        funds = 1000;
        this.status = false;
    }

    public User(String username, String password, ArrayList<Resource> userResources, int funds){
        this.username = username;
        this.password = password;
        this.userResources = userResources;
        this.funds = funds;
        this.status = false;
    }
    
    public ArrayList<Resource> getUserInventory() {
        return userResources;

    }

    public boolean getStatus(){
        return status;
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
    
    public int addFunds(int amount) {
        
        if (amount > 0 && username.equals( this.username ))
        {
            funds = funds + amount;
            return funds;
        }
        return -1;
    }

    public int deductFunds(int amount) {
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
    
    public String getPassword()
    {
        return password;
    }
    
    public boolean addResource(int resourceID, int quantity) {
        int resourceIndex = getResourceIndex(resourceID);
        if (resourceIndex != -1 && (quantity > 0)){
            int currentQuantity = getResourceQuantity(resourceID);
            userResources.get(resourceIndex).setQuantity(currentQuantity + quantity);
            return true;
        }
        else return false;
    }
    
    public boolean removeResource(int resourceID, int quantity) {
        if (getResourceIndex(resourceID) != -1 && (quantity > 0)){
            int currentQuantity = getResourceQuantity(resourceID);
            if(currentQuantity >= quantity) {
                userResources.get(resourceID - 1).setQuantity(currentQuantity - quantity);
                return true;
            }
        }
        return false;
    }

    public void setOnline(){
        this.status = true;
    }

    public void setOffline(){
        this.status = false;
    }

}
