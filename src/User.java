import java.util.*;

public class User {

    ArrayList<Resource> userResources;
    String username;
    String password;
    boolean status;
    int funds;

    /*
        This constructor will instantiate a new user with 0 resources and 1000 funds.
        This will only be used when a new account is created
     */
    public User(String username, String password, ArrayList<Resource> userResources){

        this.username = username.toLowerCase(Locale.ROOT);
        this.password = password;
        funds = 1000;
        this.status = false;
        ArrayList<Resource> defaultResources = new ArrayList<>();
        for (Resource userResource : userResources) {
            Resource r = new Resource(userResource.getId(), userResource.getCost(), 0, userResource.getName(), userResource.getValue());
            defaultResources.add(r);
        }

        this.userResources = defaultResources;
    }

    // This constructor is used when a User Object is created from existing data, to ensure that the user maintains the same values as they had previously
    public User(String username, String password, ArrayList<Resource> userResources, int funds){
        this.username = username;
        this.password = password;
        ArrayList<Resource> defaultResources = new ArrayList<>();
        for (Resource userResource : userResources) {
            Resource r = new Resource(userResource.getId(), userResource.getCost(), userResource.getQuantity(), userResource.getName(), userResource.getValue());
            defaultResources.add(r);
        }
        this.userResources = defaultResources;
        this.funds = funds;
        this.status = false;
    }

    // Method to return the user resources array
    public ArrayList<Resource> getUserInventory() {
        return userResources;

    }

    // Method to return the current user objects status
    public boolean getStatus(){
        return status;
    }

    // Method to return the index for a specific resource when provided a resourceID
    public int getResourceIndex(int resourceID) {
        for (Resource resource : userResources) {
            if (resource.getId() == resourceID) {
                return userResources.indexOf(resource);
            }
        }
        return -1;
    }

    // Method to return the current quantity of a given resource
    public int getResourceQuantity(int resourceID) {
        return userResources.get(getResourceIndex( resourceID )).getQuantity();
    }

    // Method to add funds to a User object
    public synchronized int addFunds(int amount) {
        if (amount > 0)
        {
            funds = funds + amount;
            return funds;
        }
        return -1;
    }

    // Method to remove funds from a User object
    public int deductFunds(int amount) {
        if (amount > 0 && validateCurrency( amount ))
        {
            funds = funds - amount;
            return funds;
        }
        return -1;
    }

    // Method to validate that the current user object has >= the specified amount in the parameter
    public boolean validateCurrency(int amount) {
        return funds >= amount;
    }

    // Returns the current funds value for the user
    public int getFunds() {
        return funds;
    }

    // Returns the username for the user
    public String getUsername(){
        return username;
    }

    // Returns the password for the user
    public String getPassword()
    {
        return password;
    }

    // Adds an amount of a specific resource to a user, as specified in the method parameters
    public boolean addResource(int resourceID, int quantity) {
        int resourceIndex = getResourceIndex(resourceID);
        if (resourceIndex != -1 && (quantity > 0)){
            int currentQuantity = getResourceQuantity(resourceID);
            userResources.get(resourceIndex).setQuantity(currentQuantity + quantity);
            return true;
        }
        else return false;
    }

    // Removes an amount of a specific resource from a user, as specified in the method parameters
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

    // Sets the user's status to online (true)
    public void setOnline(){
        this.status = true;
    }

    // Sets the user's status to offline (false)
    public void setOffline(){
        this.status = false;
    }

}
