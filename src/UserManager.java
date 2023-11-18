import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> userList;

    UserManager(ArrayList<User> ul){
        this.userList = ul;
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

    public int transferFunds(String source, String destination, int amount){
        if(deductFunds(source,amount) >= 0){
            addFunds(destination,amount);
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

}
