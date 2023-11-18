import java.util.ArrayList;

public class Marketplace {

    public Marketplace(){

    }

    public Marketplace(ArrayList userList, ArrayList<Resource> marketResources){
        this.userList = userList;
        this.marketResources = marketResources;
    }

    ArrayList<User> userList;
    ArrayList<Resource> marketResources = new ArrayList<Resource>();

    public boolean addResourceToMarket(int resourceID, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1){
                marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() + quantity);
                return true;
            }
        }
        /*Can't add negative number*/
        return false;
    }

    public boolean addResourceToUser(int resourceID, int quantity, String userName) {
        if (quantity > 0) {
            if (userExists(userName)){
                int user_index = getUserIndex(userName);
                int resourceIndex = getResourceIndex(resourceID);
                if (resourceIndex != -1) {
                    userList.get(user_index).addResource(resourceID, quantity, userName);

                    return true;
                }
            }
            /*Can't add negative number*/
            return false;
        }
        return false;
    }

    public boolean removeResourceFromMarket(int resourceID, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1){
                marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() - quantity);
                return true;
            }
        }
        /*Can't add negative number*/
        return false;
    }

    public boolean removeResourceFromUser(int resourceID, int quantity, String userName) {
        if (quantity > 0) {
            if (userExists(userName)){
                int user_index = getUserIndex(userName);
                int resourceIndex = getResourceIndex(resourceID);
                if (resourceIndex != -1) {
                    userList.get(user_index).removeResource(resourceID, quantity, userName);
                    return true;
                }
                /*Check for item already existing*/
            }
            /*Can't add negative number*/
            return false;
        }
        return false;
    }

    public boolean notifyUserResource(String username, int resourceID) {
        int userIndex = getUserIndex(username);
        int resourceIndex = getResourceIndex(resourceID);
        if (userIndex != -1 && resourceIndex != -1){
            System.out.println("Your " + marketResources.get(resourceIndex).getName() + " is now "  + userList.get(userIndex).userResources.get(resourceIndex).getQuantity());

            return true;
        }

        return false;
    }
    
    public boolean notifyUserCurrency(String username){
        int userIndex = getUserIndex(username);
        if (userIndex != -1){
            System.out.println("Your funds are now " + userList.get(userIndex).funds);

            return true;
        }
        return false;
    }

    public boolean validateCurrency(int amount, String userName) {
        if(amount > 0){
            int userIndex = getUserIndex(userName);
            if (userList.get(userIndex).funds >= amount){
                return true;
            }
            return false;
        }
        return false;
    }
    
    public int calculateTotal(int quantity, int resourceID) {
        if (quantity > 0){
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex == -1){
                return -1;
            }
            return marketResources.get(resourceIndex).getCost() * quantity;
        }
        return -1;
    }

    public boolean userExists(String username) {
        for (User user : userList) {
            if (user.username.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public int getUserIndex(String username) {
        for (User user : userList) {
            if (user.username.equals(username)) {
                return userList.indexOf(user);
            }
        }
        return -1;
    }

    public int getResourceIndex(int resourceID) {
        for (Resource resource : marketResources) {
            if (resource.getId() == resourceID) {
                return marketResources.indexOf(resource);
            }
        }
        return -1;
    }
    
    public int getResourceQuantity(int resourceID) {
        int resourceIndex = getResourceIndex(resourceID);
        if (resourceIndex != -1){
            return marketResources.get(resourceIndex).getQuantity();
        }
        return -1;
    }

    public Resource getResourceDetails(int resourceID) {
        int resourceIndex = getResourceIndex(resourceID);
        if (resourceIndex != -1){
            return marketResources.get(resourceIndex);
        }
        return null;
    }
    
    public int getFunds(String username) {
        int user_index = getUserIndex(username);
        if (user_index == -1){
            return -1;
        }
        return userList.get(user_index).getFunds();
    }

    public ArrayList<Resource> getUserInventory(String username) {
        int user_index = getUserIndex(username);
        if (user_index == -1){
            return null;
        }
        return userList.get(user_index).getUserInventory();
    }

    public ArrayList<Resource> getMarketInventory() {
        return marketResources;
    }

    public int addFunds(String destination_username, int amount) {
        if (amount > 0) {
            int user_index = getUserIndex(destination_username);
            if (user_index == -1){
                return -1;
            }
            userList.get(user_index).addFunds(destination_username, amount);
            return userList.get(user_index).getFunds();

        }
        /*Can't deduct negative number, return error*/
        return -1;
    }

    public int deductFunds(String source_username, int amount) {
        if (amount > 0) {
            int user_index = getUserIndex(source_username);
            if (user_index == -1){
                return -1;
            }
            userList.get(user_index).deductFunds(source_username, amount);
            return userList.get(user_index).getFunds();
        }
        /*Can't deduct negative number, return error*/
        return -1;
    }
    
    public boolean transferFunds(String source, String destination, int amount) {
        if (amount > 0){
            int sourceIndex = getUserIndex(source);
            int destinationIndex = getUserIndex(destination);
            /*Check that both source and destination user exists and not the same user*/
            if((sourceIndex != -1 && destinationIndex != -1) && (sourceIndex != destinationIndex)){
                /*Check that source user has enough funds to transfer*/
                if (getFunds(source) >= amount){
                    deductFunds(source, amount);
                    addFunds(destination,amount);
                    return true;
                } else {
                    return false;
                }
            /*If source or destination user doesn't exist*/
            } else {
                return false;
            }
        }
        return false;
    }

}
