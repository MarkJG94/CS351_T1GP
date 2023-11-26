import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
    This class is will facilitate the connection between the server and the client.
    It contains all commands and logic to parse incoming requests from clients and administrators
 */
public class SocketHandler implements Runnable{
    Socket socket;
    String username;
    UserManager userManager;
    Marketplace marketplace;
    PrintWriter printWriter;
    Scanner scanner;
    boolean userExists;

    // Administrator string, used to authenticate an administrator
    String admin = "42b0307fc70d04e46e2c189eb011259c94998921fc6b394448f4a2705453cf698f749cb733226d80f40786cb12c857122d253a5e325cdbe91ad325e75b129ab8ba88008c10a5160035e21bc92993c3647fc10fb1307049d14a51789bdca7e436d5fee2b3b4dc5c3b7e611add83edf71284764d775bd049d286c23760765263f965559f20b77b794d6365678be2ae47f8572a4fd253cef295e0b1e4412245bb63";

    // Constructor which sets up the class with the appropriate socket as decided by the server, as well as the userManager and marketplace object addresses
    SocketHandler(Socket socket, UserManager userManager, Marketplace marketplace) {

        this.socket = socket;
        this.userManager = userManager;
        this.marketplace = marketplace;
        this.userExists = false;
    }

    @Override
    public void run() {
        try {

            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            // If the first command recieved is to create a new account, call the new account method
            String command = scanner.nextLine();
            if(command.equals("NewAccount")){
                newAccountPrompt();
            }

            // Call the login method to allow users to log in to an account
            login();

            // Confirm if the user logged in successfully
            if (userExists)
            {
                // If the user is not an admin, set the status of the user object to true and add them to the socket hashmap
                if(!username.equals(admin)) {
                    userManager.setUserStatus(username, true);
                    userManager.assignToSocket(socket, username);
                }

                /*
                    Main loop body that will run until the user exists
                    This is used to parse incoming commands and then run the appropriate actions
                 */
                boolean running = true;
                while(running){
                    command = scanner.nextLine();
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(command.split("-")));
                    switch(data.get(0)){
                        case "Inventory":
                            // return the users inventory
                            getInventory(data);
                            break;
                        case "Users":
                            // return the list of users
                            getUsers(data);
                            break;
                        case "Transfer":
                            // transfer funds between two users
                            transferFunds(data);
                            break;
                        case "Buy":
                            // buy a resource from the market
                            buyResource(data);
                            break;
                        case "Sell":
                            // sell a resource to the market
                            sellResource(data);
                            break;
                        case "AddFunds":
                            // ADMINISTRATOR - command to add funds to a targeted user
                            addFunds(data);
                            break;
                        case "RemoveFunds":
                            // ADMINISTRATOR - command to remove funds from a targeted user
                            removeFunds(data);
                            break;
                        case "AddResource":
                            // ADMINISTRATOR - command to add a resource to a targeted user
                            addResource(data);
                            break;
                        case "RemoveResource":
                            // ADMINISTRATOR - command to remove a resource from a targeted user
                            removeResource(data);
                            break;
                        case "Quit":
                            // end the loop and exit the application
                            quit();
                            running = false;
                            break;
                    }
                }
            }
            else
            {
                printWriter.println("Denied");
                socket.close();
            }
        } catch (IOException e)
        {
            System.out.println(e);
        }
    }

    // Method used for test cases to test incoming commands
    public int runTest(String username, String password, String command) {
        try {

            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            if(command.equals("NewAccount")){
                newAccountPrompt();
            }

            testLogin(username, password);

            if (userExists)
            {
                if(!username.equals(admin)) {
                    userManager.setUserStatus(username, true);
                    userManager.assignToSocket(socket, username);
                }
                boolean running = true;
                while(running){
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(command.split("-")));
                    switch(data.get(0)){
                        case "Inventory":
                            return getInventory(data);
                        case "Users":
                            return getUsers(data);
                        case "Transfer":
                            return transferFunds(data);
                        case "Buy":
                            return buyResource(data);
                        case "Sell":
                            return sellResource(data);
                        case "AddFunds":
                            return addFunds(data);
                        case "RemoveFunds":
                            return removeFunds(data);
                        case "AddResource":
                            return addResource(data);
                        case "RemoveResource":
                            return removeResource(data);
                        case "Quit":
                            running = false;
                            quit();
                            break;
                    }
                    System.out.println("Error");
                    System.out.println("Invalid Command in command string");
                    return -1;
                }
            }
            else
            {
                printWriter.println("Denied");
                socket.close();
            }
        } catch (IOException e)
        {
            System.out.println(e);
        }
        return 0;
    }

    // Method used to prompt a user for a nw username and then a password.
    // Includes validation to restrict multiple users with the same username, or with restricted usernames
    private void newAccountPrompt(){
        User user;
        while(true) {
            username = scanner.nextLine();
            user = userManager.getUser(username);
            if (user == null) {
                printWriter.println("password");
                break;
            } else {
                printWriter.println("username");
            }
        }
        String password = scanner.nextLine();
        if(userManager.addUser(username,password,marketplace.marketResources)){
            printWriter.println("success");
        } else {
            printWriter.println("fail");
        }
    }

    // Method to request a users username and password and validates the user exists/the password is valid
    private void login(){
        User user = null;
        while (true) {
            username = scanner.nextLine();
            if(username.equals(admin)) {
                printWriter.println("AdminAuth");
                break;
            }
            user = userManager.getUser(username);

            if (user == null) {
                printWriter.println("Username");
            } else {
                break;
            }
        }
        if(username.equals(admin)) {
            userExists = true;
        } else
        {
            printWriter.println("Password");
            while (!userExists) {
                String password = scanner.nextLine();
                if (user.getPassword().equals(password)) {
                    userExists = true;
                    printWriter.println("Login");
                } else {
                    printWriter.println("Password");
                }
            }
        }
    }

    // Method to test logging in
    private void testLogin(String username, String password){
        User user = null;
        while (true) {
            if(username.equals(admin)) {
                printWriter.println("AdminAuth");
                break;
            }
            user = userManager.getUser(username);

            if (user == null) {
                printWriter.println("Username");
            } else {
                break;
            }
        }
        if(username.equals(admin)) {
            userExists = true;
        } else
        {
            printWriter.println("Password");
            while (!userExists) {
                if (user.getPassword().equals(password)) {
                    userExists = true;
                    printWriter.println("Login");
                } else {
                    printWriter.println("Password");
                }
            }
        }
    }

    /*
        This method will receive an array list of strings and then either return the current resources within the marketplace or;
        return a specific users resources and their current funds
     */
    public int getInventory(ArrayList<String> data) throws IOException {
        if(data.get(1).equals("Marketplace")){
            StringBuilder s = new StringBuilder();
            for(int i = 1; i<6;i++) {
                Resource r = marketplace.getResourceDetails(i);
                s.append(r.getName()).append(": ").append(r.getQuantity()).append("`");
            }
            String t = s.toString();
            printWriter.println(t);
            return 0;
        } else if (userManager.getUser(data.get(1)) != null) {
            String username = data.get(1);
            ArrayList<Resource> rl = userManager.getUserInventory(username);
            if (rl != null) {
                StringBuilder s = new StringBuilder();
                s.append("Currency: ").append(userManager.getUser(username).getFunds()).append("`");
                for (Resource r : rl) {
                    s.append(r.getName()).append(": ").append(r.getQuantity()).append("`");
                }
                String t = s.toString();
                printWriter.println(t);
                return 0;
            }
        }
        return -1;
    }

    /*
        This method will get the current list of logged-in users (excluding the administrator)
        The initiating user will be excluded from the list and given a custom message if they are the only logged on user.
     */
    private int getUsers(ArrayList<String> data) throws IOException {
        StringBuilder str = new StringBuilder();
        int count = 0;
        for( String s : userManager.getOnlineUsers()){
            if(!s.equals(data.get(1))){
                str.append(s).append(", ");
                count++;
            }
        }
        if(count == 0){
            printWriter.println("You are currently the only logged on user.");
            return count;
        } else {
            str = str.deleteCharAt(str.length()-2);
            printWriter.println(str.toString());
            return count;
        }
    }

    /*
        This method receive an array list of command strings that will initiate the transfer of funds between two users.
        It will print an error message if the user does not exist, or if the user does not have enough funds
     */
    private int transferFunds(ArrayList<String> data) throws IOException {
        String senderUsername = data.get(1);
        String receiverUsername = data.get(2);
        int amount = Integer.parseInt(data.get(3));
        int serverResponse = userManager.transferFunds(senderUsername, receiverUsername, amount);

        switch(serverResponse){
            case -2:
                printWriter.println(receiverUsername + " doesn't exist.");
                return -1;
            case -1:
                printWriter.println("You do not have enough funds!");
                return -1;
            default:
                printWriter.println(amount + " transferred successfully to " + receiverUsername);
                return 0;
        }
    }

    /*
        This method receive an array list of command strings that will initiate the buying of a resource from the marketplace
        It provides error handling if the resource ID is invalid, the user doesn't have enough funds or the marketplace has insufficient quantity
     */
    private int buyResource(ArrayList<String> data) throws IOException {
        String username = data.get(2);
        int resourceID = Integer.parseInt(data.get(3));
        int quantity = Integer.parseInt(data.get(4));
        int cost = marketplace.calculateTotalCost(quantity, resourceID);
        if (cost == -1) {
            System.out.println("Error");
            System.out.println("Invalid market resource id provided");
            return -1;
        }
        int userResponse = userManager.validateUserAndFunds(username, cost);
        if (userResponse == -2) {
            printWriter.println("You do not have enough funds for this transaction!");
            return -1;
        } else if (userResponse == -1){
            printWriter.println(username + " doesn't exist.");
            return -1;
        } else {
            boolean marketResponse = marketplace.removeResourceFromMarket(resourceID, quantity);
            if (marketResponse){
                userManager.deductFunds(username, cost);
                userManager.addResource(resourceID, quantity, username);
                printWriter.println("You have bought " + quantity + " " + marketplace.getResourceDetails(resourceID).getName() + " for " + cost);
                return 0;
            } else {
                printWriter.println("The Marketplace does not have that much " + marketplace.getResourceDetails(resourceID).getName() +"!");
                return  -1;
            }
        }
    }

    /*
        This method receive an array list of command strings that will initiate the selling of a resource from the marketplace
        It provides error handling if the resource ID is invalid or the user doesn't have enough of the resource
     */
    public int sellResource(ArrayList<String> data) throws IOException {
        String username = data.get(2);
        int resourceID = Integer.parseInt(data.get(3));
        int quantity = Integer.parseInt(data.get(4));
        int value = marketplace.calculateTotalValue(quantity, resourceID);
        if (value == -1){
            System.out.println("ERROR");
            System.out.println("Invalid resource id provided ");
            return -1;
        }
        User getUser = userManager.getUser(username);
        if (getUser == null){
            System.out.println("ERROR");
            System.out.println("Invalid username provided");
            return -1;
        }
        boolean serverResponse = userManager.removeResource(resourceID, quantity, username);
        if(serverResponse){
            marketplace.addResourceToMarket(resourceID, quantity);
            userManager.addFunds(username, value);
            printWriter.println("You have sold " + quantity + " " + marketplace.getResourceDetails(resourceID).getName() + " for " + value);
            return 0;
        } else {
            printWriter.println("You do not have enough " + marketplace.getResourceDetails(resourceID).getName() + " for this transaction!");
            return -1;
        }
    }

    /*
        This method receive an array list of command strings that will initiate adding funds to a targeted user.
        !!This is only used by the administrator!!
        It provides error handling if the username is invalid and will notify the user that the funds have been added
     */
    private int addFunds(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int quantity = Integer.parseInt(data.get(2));
        int serverResponse = userManager.addFunds(username, quantity);
        if(serverResponse == -1) {
            printWriter.println(username + " does not exist!");
            return -1;
        } else {
            userManager.notifyUser("Admin", username, quantity, 1);
            printWriter.println("You have given " + username + " " + quantity);
            return 0;
        }
    }

    /*
        This method receive an array list of command strings that will initiate removal of funds from a targeted user.
        !!This is only used by the administrator!!
        It provides error handling if the username is invalid and will notify the user that the funds have been removed
     */
    private int removeFunds(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int quantity = Integer.parseInt(data.get(2));
        int serverResponse = userManager.deductFunds(username, quantity);
        if(serverResponse == -1){
            printWriter.println(username + " does not exist!");
            return -1;
        } else if(serverResponse == -2){
            printWriter.println(username + " does not have enough funds!");
            return -1;
        } else {
            printWriter.println("Removed " + quantity + " funds from " + username);
            userManager.notifyUser("Admin", username, quantity, 0);
            return 0;
        }
    }

    /*
        This method receive an array list of command strings that will initiate adding resources to a targeted user.
        !!This is only used by the administrator!!
        It provides error handling if the username is invalid and will notify the user that the resources have been added
     */
    private int addResource(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int resourceID = Integer.parseInt(data.get(2));
        if(resourceID == -1 || resourceID > marketplace.marketResources.size()){
            System.out.println("ERROR");
            System.out.println("Invalid resource id provided");
            return -1;
        }
        int quantity = Integer.parseInt(data.get(3));

        if(username.equals("Marketplace")){
            boolean serverResponse = marketplace.addResourceToMarket(resourceID, quantity);
            if(serverResponse){
                String resourceName = marketplace.getResourceDetails(resourceID).getName();
                printWriter.println("You have add " + quantity + " " + resourceName + " to the Marketplace");
                return 0;
            } else {
                printWriter.println("Unable to add resource!");
                return -1;
            }
        } else {
            if(userManager.getUser(username) == null){
                System.out.println("ERROR");
                System.out.println("Invalid username provided in command string");
                return -1;
            }

            boolean serverResponse = userManager.addResource(resourceID, quantity, username);
            if (serverResponse) {
                String resourceName = marketplace.getResourceDetails(resourceID).getName();
                userManager.notifyUser("Admin", username, quantity, 1, resourceName);
                printWriter.println("You have given " + username + " " + quantity + " " + resourceName);
                return 0;
            } else {
                printWriter.println(username + " does not exist!");
                return -1;
            }
        }
    }

    /*
        This method receive an array list of command strings that will initiate removing resources from a targeted user.
        !!This is only used by the administrator!!
        It provides error handling if the username is invalid and will notify the user that the resource have been removed
     */
    private int removeResource(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int resourceID = Integer.parseInt(data.get(2));
        if(resourceID == -1 || resourceID > marketplace.marketResources.size()){
            System.out.println("ERROR");
            System.out.println("Invalid resource id provided");
            return -1;
        }
        int quantity = Integer.parseInt(data.get(3));
        String resourceName = marketplace.getResourceDetails(resourceID).getName();

        if(username.equals("Marketplace")){
            boolean serverResponse = marketplace.removeResourceFromMarket(resourceID, quantity);
            if(serverResponse){
                printWriter.println("You have removed " + quantity + " " + resourceName + " from the Marketplace");
                return 0;
            } else {
                printWriter.println("You cannot remove that much " + resourceName);
                return -1;
            }
        } else {
            if(userManager.getUser(username) == null){
                System.out.println("ERROR");
                System.out.println("Invalid username provided in command string");
                return -1;
            }
            boolean serverResponse = userManager.removeResource(resourceID, quantity, username);
            if (serverResponse) {
                userManager.notifyUser("Admin", username, quantity,0, resourceName);
                printWriter.println("Removed " + quantity + " " + resourceName + " from " + username);
                return 0;
            } else {
                printWriter.println(username + " does not exist!");
                return -1;
            }
        }
    }

    // Quit method that will log a user out, or confirm the server is closing and close the administrator
    private void quit() throws IOException {
        if(username.equals(admin)) {
            printWriter.println("Closing server in 10 seconds");
            socket.close();
        } else {
            printWriter.println("Logging out");
            userManager.setUserStatus(username, false);
        }
    }

}