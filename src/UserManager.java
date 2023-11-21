import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UserManager {
    private ArrayList<User> userList;
    public HashMap<User,Socket> socketUserMap;
    ArrayList<Resource> marketResources = new ArrayList<Resource>();

    UserManager(ArrayList<User> ul){
        this.userList = ul;
        socketUserMap = new HashMap<>();

    }

    public User getUser(String username){
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(String username, String password, ArrayList<Resource> resources){
        User newUser = new User(username,password,resources);
        userList.add(newUser);
    }

    public int addFunds(String username, int amount) {
        List<User> userLists = new ArrayList<User>();
        userLists = Collections.synchronizedList(userList);

        for(User u : userList){
            if(u.getUsername().equals(username))
            {
                synchronized (userLists) {
                    u.addFunds(amount);
                    return u.getFunds();
                }
            }
        }
        return -1;
    }


    public int deductFunds(String username, int amount) {
        List<User> userLists = new ArrayList<User>();
        userLists = Collections.synchronizedList(userList);
        for(User u : userList){
            if(u.getUsername().equals(username))
            {
                if(u.validateCurrency(amount)){
                    synchronized (userLists){
                    u.deductFunds(amount);
                    return u.getFunds();
                }}
                return -2;
            }
        }
        return -1;
    }

    public int transferFunds(String source, String destination, int amount) throws IOException {
        Object lock1, lock2;
        lock1=source;
        lock2 = destination;
        if(deductFunds(source,amount) >= 0){
            synchronized (lock1){
                synchronized (lock2){

            addFunds(destination,amount);
            notifyUser(source,destination,amount);
            return 1;
        }}}
        return -1;
    }

    public void setUserStatus(String username, boolean s){
        if(s){
            getUser(username).setOnline();
        } else {
            getUser(username).setOffline();
        }

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

    public boolean addResource(int resourceID, int quantity, String username) {
        List<Resource> resourceLists = new ArrayList<Resource>();
        resourceLists = Collections.synchronizedList(marketResources);
        if (quantity > 0){
            synchronized (resourceLists){
            User u = getUser(username);
            u.addResource(resourceID,quantity);
            return true;
            }}
        return false;
    }

    public boolean removeResource(int resourceID, int quantity, String username) {
        List<Resource> resourceLists = new ArrayList<Resource>();
        resourceLists = Collections.synchronizedList(marketResources);
        if (quantity > 0){
            synchronized (resourceLists){
            User u = getUser(username);
            return u.removeResource(resourceID,quantity);
        }}
        return false;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void assignToSocket(Socket s, String u){
        socketUserMap.put(getUser(u), s);
    }

    public void notifyUser(String source, String destination, int amount) throws IOException {
        User u = getUser(destination);
        if(socketUserMap.containsKey(u)){
            Socket s = socketUserMap.get(u);
            PrintWriter printWriter = new PrintWriter( s.getOutputStream(), true );
            printWriter.println("IMPORTANT" + source + " has sent you " + amount);
        }

    }

}
