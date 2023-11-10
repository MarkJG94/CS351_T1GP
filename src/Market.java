import java.util.ArrayList;

public interface Market {

    // Method to validate if the account has >= the specified amount
    boolean validateCurrency(int amount);

    // Returns an int of the current users current funds
    int getFunds();

    // Returns an int of a targeted users current funds
    int getFunds(String username);

    // Returns an arraylist of the current users inventory
    ArrayList<Integer> getUserInventory();

    // Returns an arraylist of a targeted users inventory
    ArrayList<Integer> getUserInventory(String username);

    // Returns how many of a specific resource is available
    int getResourceQuantity(int resourceID);

    // Add funds to a targeted users account
    int addFunds(String username, int amount);

    // Deduct funds to a targeted users account
    int deductFunds(String username, int amount);

    // Transfer funds from one user to another
    boolean transferFunds(String source, String destination, int amount);

    // Checks if a username is valid
    boolean userExists(String username);

    // Adds a specified number of a specific resource
    boolean addResource(int itemID, int quantity);

    // Removes a specified number of a specific resource
    boolean removeResource(int itemID, int quantity);

    // Notifies a target user the new quantity of a specific resource (or currency)
    // E.g. "Your [resourceName] is now [quantity]."
    boolean notifyUser(String username, int quantity, String resourceName);

    // Takes a quantity and multiplies it by the number of
    int calculateTotal(int quantity, int resourceID);
}
