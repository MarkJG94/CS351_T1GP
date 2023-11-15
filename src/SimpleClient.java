
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SimpleClient {

    
    SimpleClient() {

    }

    public void runClient() throws IOException, NotBoundException
    {
        
        Socket socket = new Socket("127.0.0.1", 11000);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        
        printWriter.println(username);
        
        Scanner serverScanner = new Scanner(socket.getInputStream());
        String response = serverScanner.nextLine();
        
        if(response.equals( "Password" ))
        {
            System.out.println("Enter your password, (Case Sensitive): ");
            String password = scanner.nextLine();
            
            printWriter.println(password);
            
            
        }
        response = serverScanner.nextLine();
        
        if (response.equals( "Login" ))
        {
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
// View inventory
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
}
