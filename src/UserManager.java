import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/*
    This class holds the single user list and a socket/user map to map a specific user to a specific socket.
    All the command validation will be completed here, returning either a true or false value, or an integer
 */
public class UserManager {
    private final ArrayList<User> userList;
    public HashMap<User,Socket> socketUserMap;

    // Constructor
    UserManager(ArrayList<User> ul){
        this.userList = ul;
        socketUserMap = new HashMap<>();

    }

    /*
    Method that will recieve a string and validate if that string matches the current userlist.
    If it does not, it will return null, otherwise it will return the User object.
     */
    public User getUser(String username){
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    // Method to add a new users to the user list, when provided the username, password and a list of resources
    public boolean addUser(String username, String password, ArrayList<Resource> resources){
        try {
            User newUser = new User(username, password, resources);
            userList.add(newUser);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    // Add funds method to add a specified amount to a specified user, returning a negative if the user does not exist
    public int addFunds(String username, int amount) {
        Object lock0;

        if (validateUser(username) == 0)
        {
            User u = getUser( username );
            lock0 = u;
            synchronized ( lock0 )
            {
                u.addFunds( amount );
            }
            return u.getFunds();
        }
        return -1;
    }

    // Method to validate if a user exists by getting their username and returning 0 if they do, or -1 if the do not
    public int validateUser(String username) {
        User u = getUser( username );
        if (u == null)
        {
            return -1;
        }
        return 0;
    }

    // Expanding on the previous method, this checks a user exists and then confirms if they = or > the specified amount of funds
    public int validateUserAndFunds(String username, int amount) {
        Object lock0;
        if(validateUser(username) == 0)
        {
            User u = getUser( username );
            lock0 = u;
            synchronized ( lock0 )
            {
                if(u.validateCurrency(amount))
                {
                    return 0;
                }
                return -2;
            }
        }
        return -1;
    }

    // Deduct funds method to remove a specified amount from a specified user, returning a negative if the user does not exist or doesn't have enough funds
    public int deductFunds(String username, int amount) {
        Object lock0;

        if (validateUser(username) == 0)
        {
            User u = getUser( username );
            lock0 = u;
            if(u.validateCurrency(amount)) {
                synchronized (lock0) {
                    u.deductFunds(amount);
                }
                return 0;
            }
            return -2;
        }
        return -1;
    }

    // Transfer funds method to add a specified amount to a specified user, and remove it from another user, returning a negative if the user does not exist or doesn't have enough funds
    public int transferFunds(String source, String destination, int amount) throws IOException {
        if(validateUser(destination) != 0){
            return -2;
        }

        if(deductFunds(source,amount) >= 0){
            addFunds(destination,amount);
            notifyUser(source,destination,amount,1);
            return 1;
        }
        return -1;
    }

    // Method to set the status of a specified user to either true or false
    public void setUserStatus(String username, boolean s){
        if(s){
            getUser(username).setOnline();
        } else {
            getUser(username).setOffline();
        }
    }

    // Method to iterate through the user list and return all user objects whose status is true
    public ArrayList<String> getOnlineUsers(){
        ArrayList<String> ul = new ArrayList<>();
        for(User u : userList){
            if(u.getStatus()){
                ul.add(u.getUsername());
            }
        }
        return ul;
    }

    // Method to add a specified amount of a specified resource to a specified user
    public boolean addResource(int resourceID, int quantity, String username) {
        Object lock0;
        
        if (quantity > 0){
            User u = getUser(username);
            lock0 = u;
            
            synchronized ( lock0 )
            {
                u.addResource(resourceID,quantity);
            }
            return true;
            }
        return false;
    }

    // Method to remove a specified amount of a specified resource to a specified user
    public boolean removeResource(int resourceID, int quantity, String username) {
        Object lock0;
        if (quantity > 0 ){
            User u = getUser(username);
            lock0 = u;
            synchronized ( lock0 )
            {
                return u.removeResource(resourceID,quantity);
            }
        }
        return false;
    }

    // Method to return the full userList array
    public ArrayList<User> getUserList() {
        return userList;
    }

    // Method to get the inventory of a targeted user
    public ArrayList<Resource> getUserInventory(String username){
        Object lock0;
        User u = getUser(username);
        if(u != null){
            lock0 = u;
            synchronized (lock0) {
                return u.getUserInventory();
            }
        }
        return null;
    }

    // Method to assign a socket to a specific user
    public void assignToSocket(Socket s, String u){
        socketUserMap.put(getUser(u), s);
    }

    /*
        Method to notify a specific user of a change to their account funds.
        This will utilise the socketMap to identify which socket the communication should be sent to
     */
    public void notifyUser(String source, String destination, int amount, int mode) throws IOException {
        User u = getUser(destination);
        if(socketUserMap.containsKey(u)){
            Socket s = socketUserMap.get(u);
            PrintWriter printWriter = new PrintWriter( s.getOutputStream(), true );
            if(mode == 1){
                printWriter.println("IMPORTANT" + source + " has sent you " + amount + " Funds");
            } else {
                printWriter.println("IMPORTANT" + source + " has taken " + amount + " Funds");
            }

        }
    }

    /*
        Method to notify a specific user of a change to their account resources.
        This will utilise the socketMap to identify which socket the communication should be sent to
     */
    public void notifyUser(String source, String destination, int amount, int mode, String resource) throws IOException {
        User u = getUser(destination);
        if(socketUserMap.containsKey(u)){
            Socket s = socketUserMap.get(u);
            PrintWriter printWriter = new PrintWriter( s.getOutputStream(), true );
            if(mode == 1){
                printWriter.println("IMPORTANT" + source + " has sent you " + amount + " " + resource);
            } else {
                printWriter.println("IMPORTANT" + source + " has taken " + amount + " " + resource);
            }
        }
    }
}
