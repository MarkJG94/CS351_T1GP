import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SimpleClient {
    Socket socket;
    String username;
    SimpleClient() throws IOException {
        socket = new Socket("127.0.0.1", 11000);
    }

    public void runClient() throws IOException, NotBoundException
    {


        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);

        Scanner serverScanner = new Scanner(socket.getInputStream());
        String response;

        while(true){
            System.out.println("Enter your username: ");
            username = scanner.nextLine();

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
            start();
        }

    }

    public void start() throws IOException {
        String input;
        String response;
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner( System.in );
        boolean running = true;
        while (running) {
            mainMenu();
            while (true) {
                System.out.println("Please enter a number (1-5): ");
                response = scanner.nextLine();
                if ((Integer.parseInt(response) > 0) && ((Integer.parseInt(response) < 6)))
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid entry");
                }
            }
            Scanner serverScanner = new Scanner(socket.getInputStream());
            switch (Integer.parseInt(response)) {
                case 1:
                    // View inventory
                    printWriter.println("Inventory-" + username);
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(serverScanner.nextLine().split("`")));
                    for(String s : data){
                        System.out.println(s);
                    }
                    System.out.println();
                    confirmation();
                    break;
                case 2:
                    // view logged in users
                    printWriter.println("Users-" + username);
                    System.out.println("Online Users:");
                    System.out.println(serverScanner.nextLine());
                    System.out.println();
                    confirmation();
                    break;
                case 3:
                    marketStart();
                    break;
                case 4:
                    // transferFunds
                    transferFunds();
                    confirmation();
                    break;
                case 5:
                    // quit
                    printWriter.println("Quit-" + username);
                    running = false;
                    break;
            }
        }
    }

    public void marketStart() throws IOException {

        int response;
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner serverScanner = new Scanner(socket.getInputStream());
        Scanner scanner = new Scanner( System.in );
        boolean running = true;
        while (running) {
            marketMenu();
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
                    // View listings
                    printWriter.println("Inventory-Marketplace");
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(serverScanner.nextLine().split("`")));
                    for(String s : data){
                        System.out.println(s);
                    }
                    System.out.println();
                    confirmation();
                    break;
                case 2:
                    // Buy item
                    buyItem();
                    confirmation();
                    break;
                case 3:
                    sellItem();
                    confirmation();
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
        options.add("\t 4. Transfer Funds");
        options.add("\t 5. Quit");
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

    private void transferFunds() throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner( System.in );
        Scanner serverScanner = new Scanner(socket.getInputStream());

        System.out.println("Enter the username you would like to transfer to: ");
        String input = scanner.nextLine();

        System.out.println("Amount you want to transfer: ");
        int amount = scanner.nextInt();

        printWriter.println("Transfer-" + username + "-" + input + "-" + amount);
        System.out.println(serverScanner.nextLine());

    }

    private void sellItem() throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner( System.in );
        Scanner serverScanner = new Scanner(socket.getInputStream());
        printWriter.println("Inventory-Marketplace-" + username);
        String response = serverScanner.nextLine();

        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("-")));
        boolean loop = true;
        int resourceID = 0, quantity = 0;

        while (loop) {
            System.out.println("Which item would you like to sell? (enter Q to exit)");
            for (int i = 0; i < data.size(); i++) {
                System.out.print(data.get(i) + "(" + (i + 1) + ")   ");
            }
            response = scanner.nextLine();
            if (response.equalsIgnoreCase("q")) {
                loop = false;
                break;
            } else if (Integer.parseInt(response) > 0 && Integer.parseInt(response) <= data.size()) {
                resourceID = Integer.parseInt(response);
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        while (loop) {
            System.out.println("How many would you like to sell? (enter Q to exit)");
            response = scanner.nextLine();
            if (response.equalsIgnoreCase("q")) {
                loop = false;
                break;
            } else if (Integer.parseInt(response) > 0) {
                quantity = Integer.parseInt(response);
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        if(loop) {
            printWriter.println("Sell-Marketplace-" + username + "-" + resourceID + "-" + quantity);
        }
    }

    private void checkForUpdates(){
        Scanner serverScanner = null;
        try {
            serverScanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

            System.out.println(
                    "test"
            );
            System.out.println(serverScanner.nextLine());


    }

    public void confirmation(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to continue.");
        scanner.nextLine();
    }

    private void buyItem() throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner( System.in );
        Scanner serverScanner = new Scanner(socket.getInputStream());
        printWriter.println("Inventory-Marketplace-" + username);
        String response = serverScanner.nextLine();
        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));

        boolean loop = true;
        int resourceID = 0, quantity = 0;

        while (loop) {
            System.out.println("Which item would you like to buy? (enter Q to exit)");

            int i = 1;
            for(String s : data){
                System.out.print(s.split(":")[0] + " (" + i + ")    ");
                i++;
            }
            System.out.println();
            response = scanner.nextLine();
            if (response.equalsIgnoreCase("q")) {
                loop = false;
                break;
            } else if (Integer.parseInt(response) > 0 && Integer.parseInt(response) <= data.size()) {
                resourceID = Integer.parseInt(response);
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        while (loop) {
            System.out.println("How many would you like to buy? (enter Q to exit)");
            response = scanner.nextLine();
            if (response.equalsIgnoreCase("q")) {
                loop = false;
                break;
            } else if (Integer.parseInt(response) > 0) {
                quantity = Integer.parseInt(response);
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        if(loop) {
            printWriter.println("Buy-Marketplace-" + username + "-" + resourceID + "-" + quantity);
        }
        System.out.println(serverScanner.nextLine());

    }

    public static void main(String[] args) throws IOException {
        SimpleClient simpleClient = new SimpleClient();
        try {
            simpleClient.runClient();
        } catch ( IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
