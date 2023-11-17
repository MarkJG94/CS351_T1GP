
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SimpleClient {

    User user;

    SimpleClient() {

    }

    public void runClient() throws IOException, NotBoundException
    {
        
        Socket socket = new Socket("127.0.0.1", 11000);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);

        Scanner serverScanner = new Scanner(socket.getInputStream());
        String response;

        while(true){
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();

            printWriter.println(username);

            serverScanner = new Scanner(socket.getInputStream());
            response = serverScanner.nextLine();
            if(response.equals( "Password" )){
                break;
            } else {
                System.out.println("Username not found. Please try again");
            }
        }

        int passwordAttempts = 0;
        while(true) {

            if(passwordAttempts > 4) {
                break;
            } else {
                System.out.println("Enter your password: ");
            }

            String password = scanner.nextLine();
            printWriter.println(password);

            response = serverScanner.nextLine();

            if (response.equals( "Login" )){
                break;
            } else {
                System.out.println("Invalid Password! " + (3 - passwordAttempts) + " attempts remaining.");
                passwordAttempts++;
            }
        }
        
        if (response.equals( "Login" ))
        {
//            this.user = get the user from the list
            start();
        }
        
    }
    
    public void start() {
        String input;
        int response;
        Scanner scanner = new Scanner( System.in );
        boolean running = true;
        while (running) {
            mainMenu();
            while (true) {
                System.out.println("Please enter a number (1-4): ");
                response = scanner.nextInt();
                if ((response > 0) && (response < 5))
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid entry");
                }
            }
            switch (response) {
                case 1:
                    ArrayList<Resource> resources = user.getUserInventory();
                    printUserInventory(resources);
                    break;
                case 2:
// view logged in users
                    break;
                case 3:
                    marketStart();
                    break;
                case 4:
// quit
                    running = false;
                    break;
            }
        }
    }
    
    public void marketStart() {
        marketMenu();
        String input;
        int response;
        Scanner scanner = new Scanner( System.in );
        boolean running = true;
        while (running) {
            while (true) {
                System.out.println("Please enter a number (1-4): ");
                response = scanner.nextInt();
                if ((response > 0) && (response < 5))
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid entry");
                }
            }
            switch (response) {
                case 1:
// View inventory
                    break;
                case 2:
// view logged in users
                    break;
                case 3:
                    break;
                case 4:
                    running = false;
                    break;
            }
        }
    }
    
    public void mainMenu(){
        ArrayList<String> options = new ArrayList<String>();
        options.add("Welcome to the Application");
        options.add("Please select an option from the list below;");
        options.add("\t 1. View my inventory");
        options.add("\t 2. View logged on users");
        options.add("\t 3. View MarketPlace");
        options.add("\t 4. Quit");
        for(String s : options){
            System.out.println(s);
        }
    }
    
    public void marketMenu(){
        ArrayList<String> options = new ArrayList<String>();
        options.add("Marketplace Menu");
        options.add("Please select an option from the list below;");
        options.add("\t 1. View listings");
        options.add("\t 2. Buy Items");
        options.add("\t 3. Sell Items");
        options.add("\t 4. Main Menu");
        for(String s : options){
            System.out.println(s);
        }
    }
    
    public static void main(String[] args) {
        SimpleClient simpleClient = new SimpleClient();
        try {
            simpleClient.runClient();
        } catch ( IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void printUserInventory(ArrayList<Resource> userResources){
        System.out.println("***Your Inventory***");
        for (Resource resource:userResources) {
            System.out.println(resource.getName() + "\t" + resource.getQuantity() + "\t" + resource.getValue());
        }
    }

}
