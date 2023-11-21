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
            if(command.equals(admin)) {
            } else if(command.equals("NewAccount")){
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
            e.printStackTrace();
        }
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

    private void getInventory(ArrayList<String> data) throws IOException {
        if(data.get(1).equals("Marketplace")){
            StringBuilder s = new StringBuilder();
            for(int i = 1; i<6;i++) {
                Resource r = marketplace.getResourceDetails(i);
                s.append(r.getName()).append(": ").append(r.getQuantity()).append("`");
            }
            String t = s.toString();
            printWriter.println(t);
        } else {
            User u = userManager.getUser(data.get(1));
            ArrayList<Resource> rl = u.getUserInventory();
            StringBuilder s = new StringBuilder();
            s.append("Currency: ").append(u.getFunds()).append("`");
            for(Resource r:rl){
                s.append(r.getName()).append(": ").append(r.getQuantity()).append("`");
            }
            String t = s.toString();
            printWriter.println(t);
        }
    }

    private void getUsers(ArrayList<String> data) throws IOException {
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
        } else {
            str = str.deleteCharAt(str.length()-2);
            printWriter.println(str.toString());
        }
    }

    private void transferFunds(ArrayList<String> data) throws IOException {
        String senderUsername = data.get(1);
        String receiverUsername = data.get(2);
        int amount = Integer.parseInt(data.get(3));
        int serverResponse = userManager.transferFunds(senderUsername, receiverUsername, amount);

        switch(serverResponse){
            case -2:
                printWriter.println(receiverUsername + " doesn't exist.");
                break;
            case -1:
                printWriter.println("You do not have enough funds!");
                break;
            default:
                printWriter.println(amount + " transferred successfully to " + receiverUsername);
                break;
        }
    }

    private void buyResource(ArrayList<String> data) throws IOException {
        String username = data.get(2);
        int resourceID = Integer.parseInt(data.get(3));
        int quantity = Integer.parseInt(data.get(4));
        int cost = marketplace.calculateTotalCost(quantity, resourceID);
        boolean marketResponse = marketplace.removeResourceFromMarket(resourceID, quantity);
        if(marketResponse){
            int userResponse = userManager.deductFunds(username,cost);
            switch(userResponse){
                case -2:
                    printWriter.println("You do not have enough funds for this transaction!");
                    break;
                case -1:
                    printWriter.println(username + " doesn't exist.");
                    break;
                default:
                    userManager.addResource(resourceID, quantity, username);
                    printWriter.println("You have bought " + quantity + " " + marketplace.getResourceDetails(resourceID).getName() + " for " + cost);
                    break;

            }
        } else {
            printWriter.println("The Marketplace does not have that much " + marketplace.getResourceDetails(resourceID).getName() +"!");
        }
    }

    private void sellResource(ArrayList<String> data) throws IOException {
        String username = data.get(2);
        int resourceID = Integer.parseInt(data.get(3));
        int quantity = Integer.parseInt(data.get(4));
        int value = marketplace.calculateTotalValue(quantity, resourceID);
        boolean serverResponse = userManager.removeResource(resourceID, quantity, username);
        if(serverResponse){
            marketplace.addResourceToMarket(resourceID, quantity);
            userManager.addFunds(username, value);
            printWriter.println("You have sold " + quantity + " " + marketplace.getResourceDetails(resourceID).getName() + " for " + value);
        } else {
            printWriter.println("You do not have enough " + marketplace.getResourceDetails(resourceID).getName() + " for this transaction!");
        }
    }

    private void addFunds(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int quantity = Integer.parseInt(data.get(2));
        int serverResponse = userManager.addFunds(username, quantity);
        if(serverResponse == -1) {
            printWriter.println(username + " does not exist!");
        } else {
            userManager.notifyUser("Admin", username, quantity);
            printWriter.println("You have given " + username + " " + quantity);
        }
    }

    private void removeFunds(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int quantity = Integer.parseInt(data.get(2));
        int serverResponse = userManager.deductFunds(username, quantity);
        if(serverResponse == -1){
            printWriter.println(username + " does not exist!");
        } else if(serverResponse == -2){
            printWriter.println(username + " does not have enough funds!");
        } else {
            printWriter.println("Removed " + quantity + " funds from " + username);
            userManager.notifyUser("Admin", username, (quantity * -1));
        }
    }

    private void addResource(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int resourceID = Integer.parseInt(data.get(2));
        int quantity = Integer.parseInt(data.get(3));

        if(username.equals("Marketplace")){
            boolean serverResponse = marketplace.addResourceToMarket(resourceID, quantity);
            if(serverResponse){
                String resourceName = marketplace.getResourceDetails(resourceID).getName();
                printWriter.println("You have add " + quantity + " " + resourceName + " to the Marketplace");
            } else {
                printWriter.println("Unable to add resource!");
            }
        } else {
            boolean serverResponse = userManager.addResource(resourceID, quantity, username);
            if (serverResponse) {
                String resourceName = marketplace.getResourceDetails(resourceID).getName();
                userManager.notifyUser("Admin", username, quantity, resourceName);
                printWriter.println("You have given " + username + " " + quantity + " " + resourceName);
            } else {
                printWriter.println(username + " does not exist!");
            }
        }
    }

    private void removeResource(ArrayList<String> data) throws IOException {
        String username = data.get(1);
        int resourceID = Integer.parseInt(data.get(2));
        int quantity = Integer.parseInt(data.get(3));
        String resourceName = marketplace.getResourceDetails(resourceID).getName();

        if(username.equals("Marketplace")){
            boolean serverResponse = marketplace.removeResourceFromMarket(resourceID, quantity);
            if(serverResponse){
                printWriter.println("You have removed " + quantity + " " + resourceName + " from the Marketplace");
            } else {
                printWriter.println("You cannot remove that much " + resourceName);
            }
        } else {
            boolean serverResponse = userManager.removeResource(resourceID, quantity, username);
            if (serverResponse) {
                userManager.notifyUser("Admin", username, (quantity * -1), resourceName);
                printWriter.println("Removed " + quantity + " " + resourceName + " from " + username);
            } else {
                printWriter.println(username + " does not exist!");
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



//My STUFF!!!!



            if (userExists)
                    {
                    userManager.setUserStatus(username,true);
                    while(true){
                    String command = scanner.nextLine();
                    parseCommand(username, printWriter, command);
                    }
                    }
                    else
                    {
                    printWriter.println("Denied");
                    socket.close();
                    }
                    } catch (IOException e)
                    {
                    e.printStackTrace();
                    }
                    }


public boolean parseCommand(String username, PrintWriter printWriter, String command){
        ArrayList<String> data = new ArrayList<>(Arrays.asList(command.split("-")));
        switch(data.get(0)){
        case "Inventory":
        if(data.get(1).equals("Marketplace")){
        StringBuilder s = new StringBuilder();
        for (Resource resource:marketplace.marketResources) {
        s.append(resource.getName()).append(": ").append(resource.getQuantity()).append("`");
        }
        String t = s.toString();
        printWriter.println(t);
        return true;
        } else if (marketplace.userExists(data.get(1))){
        User u = userManager.getUser(data.get(1));
        ArrayList<Resource> rl = u.getUserInventory();
        StringBuilder s = new StringBuilder();
        s.append("Currency: ").append(u.getFunds()).append("`");
        for(Resource r:rl){
        s.append(r.getName()).append(": ").append(r.getQuantity()).append("`");
        }
        String t = s.toString();
        printWriter.println(t);
        return true;
        } else {
        System.out.println("Invalid command passed.  Please check and try again");
        System.out.println("Invalid source given");
        return false;
        }
        case "Buy":
        if(data.get(1).equals("Marketplace")){
        //if resource ID is in marketResources
        if(Integer.parseInt(data.get(3)) <= marketplace.marketResources.size()){
        int amount = Integer.parseInt(data.get(2));
        int resourceId = Integer.parseInt(data.get(3));
        int total = marketplace.getResourceDetails(resourceId).getCost() * amount;
        //If marketplace contains enough of the resource to sell
        if(Integer.parseInt(data.get(2)) <= marketplace.getResourceQuantity(resourceId)){
        //if User has enough money
        if(userManager.getUser(username).getFunds() >= total){
        marketplace.removeResourceFromMarket(resourceId, amount);
        userManager.addResource(resourceId, amount, username);

        userManager.deductFunds(username, total);
        return true;
        } else {
        System.out.println("Invalid command passed.  Please check and try again");
        System.out.println("Invalid currency available in user account");
        return false;
        }
        } else {
        System.out.println("Invalid command passed.  Please check and try again");
        System.out.println("Invalid resource amount available");
        return false;
        }

        } else {
        System.out.println("Invalid command passed.  Please check and try again");
        System.out.println("Invalid resourceId given");
        return false;
        }

        } else {
        System.out.println("Invalid command passed.  Please check and try again");
        System.out.println("Invalid source given");
        return false;
        }
        case "Sell":
        break;
        case "Transfer":
        User sender = userManager.getUser(data.get(1));
        User receiver = userManager.getUser(data.get(2));

        if(receiver == null){
        printWriter.println("Invalid recipient!  Please check destination user and try again");
        return false;
        }
        int amount = Integer.parseInt(data.get(3));
        if(sender.validateCurrency(amount)){
        if(userManager.transferFunds(sender.getUsername(),receiver.getUsername(),amount) < 1){
        printWriter.println("An error has occurred");
        return false;
        } else {
        printWriter.println(amount + " transferred successfully to " + receiver.getUsername());
        printWriter.println("Your currency is now " + sender.getFunds());
        return true;
        }
        } else {
        printWriter.println("You do not have enough funds!");
        return false;
        }
        case "Users":
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
        return true;
        } else {
        str = str.deleteCharAt(str.length()-2);
        printWriter.println(str.toString());
        }
        break;
        case "Quit":
        userManager.setUserStatus(username,false);
        break;
        }
        return false;
        }
        }

