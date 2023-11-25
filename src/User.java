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
        funds = 1000;
        this.status = false;
        ArrayList<Resource> defaultResources = new ArrayList<>();
        for (int i = 0; i < userResources.size();i++){
            Resource r = new Resource(userResources.get(i).getId(),userResources.get(i).getCost(),0,userResources.get(i).getName(),userResources.get(i).getValue());
            defaultResources.add(r);
        }

        this.userResources = defaultResources;
    }

    public User(String username, String password, ArrayList<Resource> userResources, int funds){
        this.username = username;
        this.password = password;
        ArrayList<Resource> defaultResources = new ArrayList<>();
        for (int i = 0; i < userResources.size();i++){
            Resource r = new Resource(userResources.get(i).getId(),userResources.get(i).getCost(),userResources.get(i).getQuantity(),userResources.get(i).getName(),userResources.get(i).getValue());
            defaultResources.add(r);
        }
        this.userResources = defaultResources;
        this.funds = funds;
        this.status = false;
    }

    //Simple get methods
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

    public String getUsername(){
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public int getFunds() {
        return funds;
    }

    //These commands receive commands, validate, and execute them
    //For individual user resource variables
    //Returning a value to indicate their completion or rejection
    public synchronized int addFunds(int amount) {
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
        int resourceIndex = getResourceIndex(resourceID);
        if ( resourceIndex != -1 && (quantity > 0)){
            int currentQuantity = getResourceQuantity(resourceID);
            if(currentQuantity >= quantity) {
                userResources.get(resourceIndex).setQuantity(currentQuantity - quantity);
                return true;
            }
        }
        return false;
    }

    //These methods set the user status to off or online
    public void setOnline(){
        this.status = true;
    }

    public void setOffline(){
        this.status = false;
    }

}
