
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
    private String filePath = new File("").getAbsolutePath();
    private String userFilePath = filePath + "/src/UserDetails.csv";
    private String resourceFilePath = filePath + "/src/MarketDetails.csv";
    String admin = "42b0307fc70d04e46e2c189eb011259c94998921fc6b394448f4a2705453cf698f749cb733226d80f40786cb12c857122d253a5e325cdbe91ad325e75b129ab8ba88008c10a5160035e21bc92993c3647fc10fb1307049d14a51789bdca7e436d5fee2b3b4dc5c3b7e611add83edf71284764d775bd049d286c23760765263f965559f20b77b794d6365678be2ae47f8572a4fd253cef295e0b1e4412245bb63";

    SocketHandler(Socket socket, UserManager userManager, Marketplace marketplace)
    {

        this.socket = socket;
        this.userManager = userManager;
        this.marketplace = marketplace;
    }



    @Override
    public void run() {
        try {

            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            User user = null;

            boolean userExists = false;

            String command = scanner.nextLine();
            if(command.equals(admin)) {
            } else if(command.equals("NewAccount")){
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
                        case "Buy":
                            buyResource(data);
                            break;
                        case "Sell":
                            break;
                        case "Transfer":
                            transferFunds(data);
                            break;
                        case "Users":
                            getUsers(data);
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

    private void quit() throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        if(username.equals(admin)) {
            printWriter.println("Closing server in 10 seconds");
            socket.close();
        } else {
            printWriter.println("Logging out");
            userManager.setUserStatus(username, false);
        }
    }

    private void getUsers(ArrayList<String> data) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
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
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        User sender = userManager.getUser(data.get(1));
        User receiver = userManager.getUser(data.get(2));
        if(receiver == null){
            printWriter.println("Invalid user!");
        } else {
            int amount = Integer.parseInt(data.get(3));
            if (sender.validateCurrency(amount)) {
                if (userManager.transferFunds(sender.getUsername(), receiver.getUsername(), amount) < 1) {
                    printWriter.println("An error has occurred");
                } else {
                    printWriter.println(amount + " transferred successfully to " + receiver.getUsername());
                }
            } else {
                printWriter.println("You do not have enough funds!");
            }
        }
    }

    private void buyResource(ArrayList<String> data) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        User u = userManager.getUser(data.get(2));
        int resourceID = Integer.parseInt(data.get(3));
        int quantity = Integer.parseInt(data.get(4));
        int cost = marketplace.calculateTotal(quantity, resourceID);
        if(marketplace.getResourceQuantity(resourceID) >= quantity){
            if(u.getFunds() >= marketplace.calculateTotal(quantity, resourceID)){
                marketplace.removeResourceFromMarket(resourceID, quantity);
                userManager.addResource(resourceID, quantity,u.getUsername());
                userManager.deductFunds(u.getUsername(),cost);
                printWriter.println("You have bought " + quantity + " " + marketplace.getResourceDetails(resourceID).getName() + " for " + cost);
            } else {
                printWriter.println("You do not have enough funds for this transaction! (Have: " + u.getFunds() + " Need:" + cost + ")");
            }
        } else {
            printWriter.println("The Marketplace does not have that much " + marketplace.getResourceDetails(resourceID).getName() +"!");
        }
    }

    private void getInventory(ArrayList<String> data) throws IOException {
        Scanner scanner = new Scanner(socket.getInputStream());
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
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
}
