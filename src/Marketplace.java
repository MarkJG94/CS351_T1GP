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
            for (Resource resource : marketResources) {
                if (resource.getId() == resourceId) {
                    /*If exists, update the quantity and return true*/
                    resource.setQuantity(resource.getQuantity() + quantity);
                    return true;
                }
            }
        }
        /*Can't add negative number*/
        return false;
    }

    @Override
    public boolean removeResource(int resourceId, int quantity) {
        if (quantity > 0) {
            /*Check for Item already existing*/
            for (Resource resource : marketResources) {
                if (resource.getId() == resourceId) {
                    /*If exists, update the quantity and return true*/
                    resource.setQuantity(resource.getQuantity() - quantity);
                    return true;
                } else {
                    /*Item doesn't exist*/
                    return false;
                }
            }
        }
        /*Can't deduct negative number*/
        return false;
    }



    @Override
    public boolean notifyUser(String username, int quantity, String resourceName) {
        return false;
    }

    @Override
    public boolean validateCurrency(int amount) {
        return false;
    }

    @Override
    public int calculateTotal(int quantity, int resourceID) {
        return 0;
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public int getResourceDetails(int resourceID) {
        return 0;
    }

    @Override
    public int getFunds() {
        return 0;
    }

    @Override
    public int getFunds(String username) {
        for (User user : userList) {
            if (user.username.equals(username)) {
                return user.funds;
            } else {
                return -1;
            }
        }
        return -1;
    }

    @Override
    public ArrayList<Integer> getUserInventory() {
        return null;
    }

    @Override
    public ArrayList<Integer> getUserInventory(String username) {
        return null;
    }

    @Override
    public int addFunds(String destination_username, int amount) {
        if (amount > 0) {
            for (User user : userList) {
                /*If user exists, add funds*/
                if (user.username.equals(destination_username)) {
                    user.funds = user.funds + amount;
                    return user.funds;
                } else{
                    /*If user doesn't exist, return error*/
                    return -1;
                }
            }
        }
        /*Can't add negative number, return error*/
        return -1;
    }

    @Override
    public int deductFunds(String destination_username, int amount) {
        if (amount > 0) {
            for (User user : userList) {
                /*If user exists, deduct funds*/
                if (user.username.equals(destination_username)) {
                    user.funds = user.funds - amount;
                    return user.funds;
                } else{
                    /*If user doesn't exist, return error*/
                    return -1;
                }
            }
        }
        /*Can't deduct negative number, return error*/
        return -1;
    }

    @Override
    public boolean transferFunds(String source, String destination, int amount) {
        return false;
    }


}
