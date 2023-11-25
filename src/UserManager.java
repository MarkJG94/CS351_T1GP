import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UserManager {
    private ArrayList<User> userList;
    public HashMap<User,Socket> socketUserMap;
    
    UserManager(ArrayList<User> ul){
        this.userList = ul;
        socketUserMap = new HashMap<>();

    }

    //Get methods
    public User getUser(String username){
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

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

    public ArrayList<String> getOnlineUsers(){
        ArrayList<String> ul = new ArrayList<>();
        for(User u : userList){
            if(u.getStatus()){
                ul.add(u.getUsername());
            }
        }
        return ul;
    }


    //Adding a new user to the userlist
    public boolean addUser(String username, String password, ArrayList<Resource> resources){
        try {
            User newUser = new User(username, password, resources);
            userList.add(newUser);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    //Validates user and requests user to add, remove, or transfer funds or resources if appropriate
    //Locks and synchronises the user to prevent synchronisation issues
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

    public int transferFunds(String source, String destination, int amount) throws IOException {
        if(validateUser(destination) != 0){
            return -2;
        }

        if(deductFunds(source,amount) >= 0){
            addFunds(destination,amount);
            notifyUser(source,destination,amount);
            return 1;
        }
        return -1;
    }

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


    //Validates user and user funds
    public int validateUser(String username) {
        Object lock0;
        User u = getUser( username );
        if (u == null)
        {
            return -1;
        }
        return 0;
    }

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


    // Request user to set appropriate user status
    public void setUserStatus(String username, boolean s){
        if(s){
            getUser(username).setOnline();
        } else {
            getUser(username).setOffline();
        }

    }

    //Assigns a user to a socket to facilitate safe communication
    public void assignToSocket(Socket s, String u){
        socketUserMap.put(getUser(u), s);
    }

    // Validate user connection and send notifications to user
    public void notifyUser(String source, String destination, int amount) throws IOException {
        User u = getUser(destination);
        if(socketUserMap.containsKey(u)){
            Socket s = socketUserMap.get(u);
            PrintWriter printWriter = new PrintWriter( s.getOutputStream(), true );
            printWriter.println("IMPORTANT" + source + " has sent you " + amount + " Funds");
        }
    }

    public void notifyUser(String source, String destination, int amount, String resource) throws IOException {
        User u = getUser(destination);
        if(socketUserMap.containsKey(u)){
            Socket s = socketUserMap.get(u);
            PrintWriter printWriter = new PrintWriter( s.getOutputStream(), true );
            printWriter.println("IMPORTANT" + source + " has sent you " + amount + " " + resource);
        }
    }
}
