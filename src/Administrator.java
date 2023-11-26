import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
    This class is used as the front-end to run the administrator command line from the server and can be used to;
    - Deduct funds from a user
    - Add funds to a user
    - Deduct resources to a user
    - Add resources to a user
    - Remove resources from a Marketplace
    - Add resources to a the Marketplace
    - Transfer funds between users
    - Save the Marketplace and Userlist to CSV
    - Close the server
 */
public class Administrator extends InputReader implements Runnable {

    Socket socket;
    PrintWriter printWriter;
    Scanner serverScanner;
    InputReader inputReader;
    String username = "admin";

    // Stores the password used to authenticate an admin
    String pw;

    Administrator(String pw) throws IOException {
        socket = new Socket("127.0.0.1", 11000);
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        serverScanner = new Scanner(socket.getInputStream());
        inputReader = new InputReader();
        scanner = new Scanner(System.in);
        this.pw = pw;
    }

    // Method to print the main menu options to screen
    public void mainMenu(){
        ArrayList<String> options = new ArrayList<>();
        options.add("Server Administrator Menu");
        options.add("Please select an option from the list below;");
        options.add("\t 1. View logged on users");
        options.add("\t 2. View MarketPlace");
        options.add("\t 3. Transfer Funds");
        options.add("\t 4. Add Funds");
        options.add("\t 5. Remove Funds");
        options.add("\t 6. Shutdown Server");
        for(String s : options){
            System.out.println(s);
        }
    }

    // Method to print the marketplace menu to screen
    public void marketMenu(){
        ArrayList<String> options = new ArrayList<>();
        options.add("Marketplace Menu");
        options.add("Please select an option from the list below;");
        options.add("\t 1. View listings");
        options.add("\t 2. Add Items to Marketplace");
        options.add("\t 3. Remove Items from Marketplace");
        options.add("\t 4. Add Items to User");
        options.add("\t 5. Remove Items from User");
        options.add("\t 6. Main Menu");
        for(String s : options){
            System.out.println(s);
        }
    }

    // start method for the thread which acts as the main driver for the Administrator class
    public void start() throws IOException {
        int val;
        printWriter.println(pw);
        printWriter.println(pw);

        boolean running = false;
        if(serverScanner.nextLine().equals("AdminAuth")) {
            running = true;
        } else
        {
            System.out.println("Error validating administrator!");
        }

        // Main while loop
        while (running) {
            mainMenu();
            while (true) {
                System.out.println("Please enter a number (1-6): ");
                val = inputReader.getNumericResponse();
                if (val > 0 && (val < 7))
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid entry");
                }
            }

            // Switch case for the main menu user input
            switch (val) {
                case 1:
                    // View logged in users
                    printWriter.println("Users-" + username);
                    System.out.println("Online Users:");
                    System.out.println(serverScanner.nextLine());
                    System.out.println();
                    confirmation();
                    break;
                case 2:
                    // Launch marketplace menu
                    marketStart();
                    break;
                case 3:
                    // Transfer funds between users
                    transferFunds();
                    confirmation();
                    break;
                case 4:
                    // Add funds to user
                    addFunds();
                    confirmation();
                    break;
                case 5:
                    // Remove funds from user
                    removeFunds();
                    confirmation();
                    break;
                case 6:
                    // quit
                    printWriter.println("Quit-" + username);
                    System.out.println(serverScanner.nextLine());
                    running = false;
                    break;
            }
        }
        socket.close();
    }

    // Looping method for the marketplace menu and its options
    public void marketStart() {
        int val;
        boolean running = true;
        while (running) {
            marketMenu();
            while (true) {
                System.out.println("Please enter a number (1-6): ");
                val = inputReader.getNumericResponse();
                if ((val > 0) && (val < 7))
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid entry");
                }
            }

            // Switch case for each available option
            switch (val) {
                case 1:
                    // View Marketplace resouces
                    printWriter.println("Inventory-Marketplace");
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(serverScanner.nextLine().split("`")));
                    for(String s : data){
                        System.out.println(s);
                    }
                    System.out.println();
                    confirmation();
                    break;
                case 2:
                    // Add an item to the marketplace
                    addItemMarket();
                    confirmation();
                    break;
                case 3:
                    // Remove an item from the marketplace
                    removeItemMarket();
                    confirmation();
                    break;
                case 4:
                    // Add an item to a user
                    addItemUser();
                    confirmation();
                    break;
                case 5:
                    // Remove an item from a user
                    removeItemUser();
                    confirmation();
                    break;
                case 6:
                    // Return to main menu loop
                    running = false;
                    break;
            }
        }
    }

    // Method to remove funds from a targeted user
    private void removeFunds() {
        System.out.println("Enter the username you would like to remove funds from: ");
        String source = inputReader.getResponse();

        System.out.println("Amount you want to transfer: ");
        int val = inputReader.getNumericResponse();
        if(val > 0 ) {
            int amount = val;

            printWriter.println("RemoveFunds-" + source + "-" + amount);
            System.out.println(serverScanner.nextLine());
        } else {
            System.out.println("Invalid entry.");
        }
    }

    // Method to add funds to a targeted user
    private void addFunds() {
        System.out.println("Enter the username you would like to add funds to: ");
        String source = inputReader.getResponse();

        System.out.println("Amount you want to transfer: ");
        int val = inputReader.getNumericResponse();
        if(val > 0) {
            int amount = val;

            printWriter.println("AddFunds-" + source + "-" + amount);
            System.out.println(serverScanner.nextLine());
        } else {
            System.out.println("Invalid entry.");
        }
    }

    // Method to add a resource to a user
    private void addItemUser() {

        printWriter.println("Inventory-Marketplace-" + username);
        String response = serverScanner.nextLine();
        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));

        boolean loop = true;
        int resourceID = 0;

        System.out.println("Enter the username you would like to add resources to: ");
        String source = inputReader.getResponse();

        System.out.println("Enter the resource ID you'd like to add: ");

        while (loop) {
            int i = 1;
            for (String s : data) {
                System.out.print(s.split(":")[0] + " (" + i + ")    ");
                i++;
            }
            System.out.println();
            int val = inputReader.getNumericResponse();
            if (val == -2) {
                loop = false;
                break;
            } else if (val > 0 && val <= data.size()) {
                resourceID = val;
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        if(loop) {
            System.out.println("Amount you want to transfer: ");
            int val = inputReader.getNumericResponse();
            if (val > 0) {
                int amount = val;
                printWriter.println("AddResource-" + source + "-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }
        }
    }

    // Method to remove an item from the Marketplace
    private void removeItemMarket() {

        printWriter.println("Inventory-Marketplace-" + username);
        String response = serverScanner.nextLine();
        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));

        boolean loop = true;
        int resourceID = 0;

        System.out.println("Enter the resource ID you'd like to remove: ");

        while (loop) {
            int i = 1;
            for (String s : data) {
                System.out.print(s.split(":")[0] + " (" + i + ")    ");
                i++;
            }
            System.out.println();
            int val = inputReader.getNumericResponse();
            if (val == -2) {
                loop = false;
                break;
            } else if (val > 0 && val <= data.size()) {
                resourceID = val;
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        if(loop) {

            System.out.println("Amount you want to remove: ");
            int val = inputReader.getNumericResponse();
            if(val > 0) {
                int amount = val;

                printWriter.println("RemoveResource-Marketplace-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }

        }
    }

    // Method to remove a resource from a user
    private void removeItemUser() {
        printWriter.println("Inventory-Marketplace-" + username);
        String response = serverScanner.nextLine();
        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));

        boolean loop = true;
        int resourceID = 0;
        System.out.println("Enter the username you would like to add resources to: ");
        String source = inputReader.getResponse();

        System.out.println("Enter the resource ID you'd like to remove: ");

        while (loop) {
            int i = 1;
            for (String s : data) {
                System.out.print(s.split(":")[0] + " (" + i + ")    ");
                i++;
            }
            System.out.println();
            int val = inputReader.getNumericResponse();
            if (val == -2) {
                loop = false;
                break;
            } else if (val > 0 && val <= data.size()) {
                resourceID = val;
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        if(loop) {
            System.out.println("Amount you want to transfer: ");
            int val = inputReader.getNumericResponse();
            if(val > 0) {
                int amount = val;

                printWriter.println("RemoveResource-" + source + "-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }
        }
    }

    // Method to add a resource to the Marketplace
    private void addItemMarket() {
        printWriter.println("Inventory-Marketplace-" + username);
        String response = serverScanner.nextLine();
        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));

        boolean loop = true;
        int resourceID = 0;

        System.out.println("Enter the resource ID you'd like to add: ");

        while (loop) {
            int i = 1;
            for (String s : data) {
                System.out.print(s.split(":")[0] + " (" + i + ")    ");
                i++;
            }
            System.out.println();
            int val = inputReader.getNumericResponse();
            if (val == -2) {
                loop = false;
                break;
            } else if (val > 0 && val <= data.size()) {
                resourceID = val;
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }

        if(loop) {
            System.out.println("Amount you want to transfer: ");
            int val = inputReader.getNumericResponse();
            if (val > 0) {
                int amount = val;

                printWriter.println("AddResource-Marketplace-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }
        }
    }

    // Method to transfer funds between two users
    private void transferFunds() throws IOException {
        System.out.println("Enter the username you would like to transfer from: ");
        String source = inputReader.getResponse();

        System.out.println("Enter the username you would like to transfer to: ");
        String destination = inputReader.getResponse();
        
        System.out.println("Amount you want to transfer: ");
        int val = inputReader.getNumericResponse();
        if(val == -2){

        } else if(val > 0) {
            int amount = val;

            printWriter.println("Transfer-" + source + "-" + destination + "-" + amount);
            System.out.println(serverScanner.nextLine());
        } else {
            System.out.println("Invalid entry.");
        }

    }

    // Confirmation method that will await any user input before continuing
    public void confirmation(){
        System.out.println("Press enter to continue.");
        scanner.nextLine();
    }

    // Run method that will call the start method
    @Override
    public void run() {
        try {
            start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
