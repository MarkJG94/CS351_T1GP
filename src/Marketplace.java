import java.util.ArrayList;
import java.util.List;

public class Marketplace implements Market {

    Server server = new Server();
    ArrayList<User> userList = server.getUserList();
    ArrayList<Resource> marketResources = new ArrayList<Resource>();

    @Override
    public boolean addResource(int resourceId, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceId);
            if (resourceIndex != -1){
                marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() + quantity);
                return true;
            }
        }
        /*Can't add negative number*/
        return false;
    }

    @Override
    public boolean removeResource(int resourceId, int quantity) {
        if (quantity > 0) {
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceId);
            if (resourceIndex != -1){
                marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() - quantity);
                return true;
            }
        }
        /*Can't add negative number*/
        return false;
    }

    @Override
    public boolean notifyUserResource(String username, int resourceID) {
        int userIndex = getUserIndex(username);
        int resourceIndex = getResourceIndex(resourceID);
        if (userIndex != -1 && resourceIndex != -1){
            System.out.println("Your " + marketResources.get(resourceIndex).getName() + " is now "  + userList.get(userIndex).userResources.get(resourceIndex).getQuantity());

            return true;
        }

        return false;
    }

    @Override
    public boolean notifyUserCurrency(String username){
        int userIndex = getUserIndex(username);
        if (userIndex != -1){
            System.out.println("Your funds are now " + userList.get(userIndex).funds);

            return true;
        }
        return false;
    }

    @Override
    public boolean validateCurrency(int amount) {
        return false;
    }

    @Override
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

    @Override
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

    @Override
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

    //    Not Implemented yet.  Marketplace has a list of Users, and needs an identifier
    @Override
    public int getFunds() {
        return 0;
    }

    @Override
    public int getFunds(String username) {
        int user_index = getUserIndex(username);
        if (user_index == -1){
            return -1;
        }
        return userList.get(user_index).funds;
    }

//    Not Implemented yet.  Marketplace has a list of Users, and needs an identifier
    @Override
    public ArrayList<Resource> getUserInventory() {
        return null;
    }

    @Override
    public ArrayList<Resource> getUserInventory(String username) {
        int user_index = getUserIndex(username);
        if (user_index == -1){
            return null;
        }
        return userList.get(user_index).userResources;
    }

    @Override
    public int addFunds(String destination_username, int amount) {
        if (amount > 0) {
            int user_index = getUserIndex(destination_username);
            if (user_index == -1){
                return -1;
            }
            userList.get(user_index).funds += amount ;
            return userList.get(user_index).funds;

        }
        /*Can't deduct negative number, return error*/
        return -1;
    }

    @Override
    public int deductFunds(String destination_username, int amount) {
        if (amount > 0) {
            int user_index = getUserIndex(destination_username);
            if (user_index == -1){
                return -1;
            }
            userList.get(user_index).funds -= amount ;
            return userList.get(user_index).funds;
        }
        /*Can't deduct negative number, return error*/
        return -1;
    }

    @Override
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
