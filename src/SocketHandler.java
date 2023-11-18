
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
                System.out.println(password);
                if (user.getPassword().equals(password)) {
                    userExists = true;
                    printWriter.println("Login");
                } else {
                    printWriter.println("Password");
                }
            }
            
            if (userExists)
            {
                while(true){
                    String command = scanner.nextLine();
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(command.split("-")));
                    switch(data.get(0)){
                        case "Inventory":
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
                            break;
                        case "Buy":
                            break;
                        case "Sell":
                            break;
                        case "Transfer":
                            User sender = userManager.getUser(data.get(1));
                            User receiver = userManager.getUser(data.get(2));
                            if(receiver == null){
                                printWriter.println("Invalid user!");
                                break;
                            }
                            int amount = Integer.parseInt(data.get(3));
                            if(sender.validateCurrency(amount)){
                                userManager.transferFunds(sender.getUsername(),receiver.getUsername(),amount);
                            } else {
                                printWriter.println("You do not have enough funds!");
                            }
                            printWriter.println("Your currency is " + sender.getFunds());
                            break;
                        case "Userlist":
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
}
