import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class UserManager {
    private ArrayList<User> userList;
    public HashMap<User,Socket> socketUserMap;

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

    public int addFunds(String username, int amount) {

        for(User u : userList){
            if(u.getUsername().equals(username))
            {
                u.addFunds(amount);
                return u.getFunds();
            }
        }
        return -1;
    }


    public int deductFunds(String username, int amount) {
        for(User u : userList){
            if(u.getUsername().equals(username))
            {
                if(u.validateCurrency(amount)){
                    u.deductFunds(amount);
                    return u.getFunds();
                }
                return -2;
            }
        }
        return -1;
    }

    public int transferFunds(String source, String destination, int amount) throws IOException {
        if(deductFunds(source,amount) >= 0){
            addFunds(destination,amount);
            //notifyUser(source,destination,amount);
            return 1;
        }
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
        if (quantity > 0){
            User u = getUser(username);
            u.addResource(resourceID,quantity);
            return true;
            }
        return false;
    }

    public boolean removeResource(int resourceID, int quantity, String username) {
        if (quantity > 0){
            User u = getUser(username);
            return u.removeResource(resourceID,quantity);
        }
        return false;
    }

    public void assignToSocket(Socket s, String u){
        socketUserMap.put(getUser(u), s);
    }

    public void notifyUser(String source, String destination, int amount) throws IOException {
        User u = getUser(destination);
        if(socketUserMap.containsKey(u)){
            Socket s = socketUserMap.get(u);
            PrintWriter printWriter = new PrintWriter( s.getOutputStream(), true );
            printWriter.println(source + " has sent you " + amount);
        }

    }

}
