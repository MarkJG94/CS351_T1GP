
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SocketHandler implements Runnable{
    Socket socket;
    UserManager userManager;
    Marketplace marketplace;

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
            PrintWriter printWriter = new PrintWriter( socket.getOutputStream(), true );
            String username;
            User user;

            boolean userExists = false;

            while(true) {
                username = scanner.nextLine();
                user = userManager.getUser(username);
                if (user == null) {
                    printWriter.println("Username");
                } else {
                    break;
                }
            }
            printWriter.println("Password");
            while(!userExists) {
                String password = scanner.nextLine();
                if (user.getPassword().equals(password)) {
                    userExists = true;
                    printWriter.println("Login");
                } else {
                    printWriter.println("Password");
                }
            }
            
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
