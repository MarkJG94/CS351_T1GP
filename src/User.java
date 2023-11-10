import java.util.ArrayList;

public class User  {

    ArrayList<Resource> userResources;
    String username;
    int funds;

    public User(String username, ArrayList<Resource> userResources){
        this.username = username.toLowerCase();
        this.userResources = userResources;
        funds = 1000;
    }

    public User(String username, ArrayList<Resource> userResources, int funds){
        this.username = username.toLowerCase();
        this.userResources = userResources;
        this.funds = funds;
    }
}
