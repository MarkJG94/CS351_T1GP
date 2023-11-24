import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SocketHandler implements Runnable{
    Socket socket;
    String username;
    UserManager userManager;
    Marketplace marketplace;
    PrintWriter printWriter;
    Scanner scanner;
    boolean userExists;
    String admin = "42b0307fc70d04e46e2c189eb011259c94998921fc6b394448f4a2705453cf698f749cb733226d80f40786cb12c857122d253a5e325cdbe91ad325e75b129ab8ba88008c10a5160035e21bc92993c3647fc10fb1307049d14a51789bdca7e436d5fee2b3b4dc5c3b7e611add83edf71284764d775bd049d286c23760765263f965559f20b77b794d6365678be2ae47f8572a4fd253cef295e0b1e4412245bb63";

    SocketHandler(Socket socket, UserManager userManager, Marketplace marketplace)
    {

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

            String command = scanner.nextLine();
            if(command.equals("NewAccount")){
                newAccountPrompt();
            }

            login();

            if (userExists)
            {
                if(!username.equals(admin)) {
                    userManager.setUserStatus(username, true);
                    userManager.assignToSocket(socket, username);
                }
                boolean running = true;
                while(running){
                    command = scanner.nextLine();
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(command.split("-")));
                    switch(data.get(0)){
                        case "Inventory":
                            getInventory(data);
                            break;
                        case "Users":
                            getUsers(data);
                            break;
                        case "Transfer":
                            transferFunds(data);
                            break;
                        case "Buy":
                            buyResource(data);
                            break;
                        case "Sell":
                            sellResource(data);
                            break;
                        case "AddFunds":
                            addFunds(data);
                            break;
                        case "RemoveFunds":
                            removeFunds(data);
                            break;
                        case "AddResource":
                            addResource(data);
                            break;
                        case "RemoveResource":
                            removeResource(data);
                            break;
                        case "Quit":
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