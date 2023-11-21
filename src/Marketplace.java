import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Marketplace {


    public Marketplace(ArrayList<Resource> resourceList) {
        this.marketResources = resourceList;
    }

    ArrayList<User> userList;
    ArrayList<Resource> marketResources = new ArrayList<Resource>();

    public boolean addResourceToMarket(int resourceID, int quantity) {
        List<Resource> resourceLists = new ArrayList<Resource>();
        if (quantity > 0) {
            resourceLists = Collections.synchronizedList(marketResources);
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1) {
                synchronized (resourceLists) {
                    marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() + quantity);
                    return true;
                }
            }
        }
        /*Can't add negative number*/
        return false;
    }

    public boolean addResourceToUser(int resourceID, int quantity, String userName) {
        List<User> userLists = new ArrayList<User>();
        if (quantity > 0) {
            if (userExists(userName)) {
                userLists = Collections.synchronizedList(userList);
                int user_index = getUserIndex(userName);
                int resourceIndex = getResourceIndex(resourceID);
                if (resourceIndex != -1) {
                    synchronized (userLists) {
                        userList.get(user_index).addResource(resourceID, quantity);

                        return true;
                    }
                }
            }
            /*Can't add negative number*/
            return false;
        }
        return false;
    }

    public boolean removeResourceFromMarket(int resourceID, int quantity) {
        List<Resource> resourceLists = new ArrayList<Resource>();
        if (quantity > 0) {
            resourceLists = Collections.synchronizedList(marketResources);
            /*Check for item already existing*/
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex != -1) {
                synchronized (resourceLists) {
                    marketResources.get(resourceIndex).setQuantity(marketResources.get(resourceIndex).getQuantity() - quantity);
                    return true;
                }
            }
        }
        /*Can't add negative number*/
        return false;
    }

    public boolean removeResourceFromUser(int resourceID, int quantity, String userName) {
        List<User> userLists = new ArrayList<User>();
        if (quantity > 0) {
            if (userExists(userName)) {
                userLists = Collections.synchronizedList(userList);
                int user_index = getUserIndex(userName);
                int resourceIndex = getResourceIndex(resourceID);
                if (resourceIndex != -1) {
                    synchronized (userLists) {
                        userList.get(user_index).removeResource(resourceID, quantity);
                        return true;
                    }
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
        if (userIndex != -1 && resourceIndex != -1) {
            System.out.println("Your " + marketResources.get(resourceIndex).getName() + " is now " + userList.get(userIndex).userResources.get(resourceIndex).getQuantity());

            return true;
        }

        return false;
    }

    public boolean notifyUserCurrency(String username) {
        int userIndex = getUserIndex(username);
        if (userIndex != -1) {
            System.out.println("Your funds are now " + userList.get(userIndex).funds);

            return true;
        }
        return false;
    }

    public boolean validateCurrency(int amount, String userName) {
        if (amount > 0) {
            int userIndex = getUserIndex(userName);
            if (userList.get(userIndex).funds >= amount) {
                return true;
            }
            return false;
        }
        return false;
    }

    public int calculateTotal(int quantity, int resourceID) {
        if (quantity > 0) {
            int resourceIndex = getResourceIndex(resourceID);
            if (resourceIndex == -1) {
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
        if (resourceIndex != -1) {
            return marketResources.get(resourceIndex).getQuantity();
        }
        return -1;
    }

    public Resource getResourceDetails(int resourceID) {
        if (marketResources.size() > resourceID - 1) {
            return marketResources.get(resourceID - 1);
        } else {
            return null;
        }
    }

    public int getFunds(String username) {
        int user_index = getUserIndex(username);
        if (user_index == -1) {
            return -1;
        }
        return userList.get(user_index).getFunds();
    }

    public ArrayList<Resource> getUserInventory(String username) {
        int user_index = getUserIndex(username);
        if (user_index == -1) {
            return null;
        }
        return userList.get(user_index).getUserInventory();
    }

    public ArrayList<Resource> getMarketResources() {
        return marketResources;
    }

}
