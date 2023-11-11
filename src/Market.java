import java.util.ArrayList;

public interface Market {

    // Method to validate if the account has >= the specified amount
    boolean validateCurrency(int amount);

    // Returns an int of the current users current funds
    int getFunds();

    // Returns an int of a targeted users current funds
    int getFunds(String username);

    // Returns an arraylist of the current users inventory
    ArrayList<Resource> getUserInventory();

    // Returns an arraylist of a targeted users inventory
    ArrayList<Resource> getUserInventory(String username);

    // Returns how many of a specific resource is available
    int getResourceQuantity(int resourceID);

    // Adds funds to a targeted users account
    int addFunds(String username, int amount);

    // Deducts funds to a targeted users account
    int deductFunds(String username, int amount);

    // Transfers funds from one user to another
    boolean transferFunds(String source, String destination, int amount);

    // Checks if a username is valid
    boolean userExists(String username);

    // Adds a specified number of a specific resource
    boolean addResource(int itemID, int quantity, String username);

    // Removes a specified number of a specific resource
    boolean removeResource(int itemID, int quantity, String username);



    //    Are we changing the quantity here using the parameter, or informing after a change?

    // Notifies a target user the new quantity of a specific resource (or currency)
    // E.g. "Your [resourceName] is now [quantity]."
    boolean notifyUserResource(String username, int resourceID);


    // Notifies a target user the new quantity of a specific resource (or currency)
    // E.g. "Your [resourceName] is now [quantity]."
    boolean notifyUserCurrency(String username);




    // Takes a quantity and multiplies it by the number of
    int calculateTotal(int quantity, int resourceID);
}
