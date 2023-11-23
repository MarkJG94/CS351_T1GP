import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Administrator extends InputReader implements Runnable {

    Socket socket;
    PrintWriter printWriter;
    Scanner serverScanner;
    InputReader inputReader;
    String username = "admin";
    String pw = "42b0307fc70d04e46e2c189eb011259c94998921fc6b394448f4a2705453cf698f749cb733226d80f40786cb12c857122d253a5e325cdbe91ad325e75b129ab8ba88008c10a5160035e21bc92993c3647fc10fb1307049d14a51789bdca7e436d5fee2b3b4dc5c3b7e611add83edf71284764d775bd049d286c23760765263f965559f20b77b794d6365678be2ae47f8572a4fd253cef295e0b1e4412245bb63";
    Administrator() throws IOException {
        socket = new Socket("127.0.0.1", 11000);
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        serverScanner = new Scanner(socket.getInputStream());
        inputReader = new InputReader();
    }

    public void mainMenu(){
        ArrayList<String> options = new ArrayList<String>();
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

    public void marketMenu(){
        ArrayList<String> options = new ArrayList<String>();
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

    public void start() throws IOException {
        String response;
        int val;
        printWriter.println(pw);
        printWriter.println(pw);

        boolean running = false;
        if(serverScanner.nextLine().equals("AdminAuth")) {
            System.out.println("Authentication Successful!");
            System.out.println();
            running = true;
        } else
        {
            System.out.println("Error validating administrator!");
        }
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
            switch (val) {
                case 1:
                    // view logged in users
                    printWriter.println("Users-" + username);
                    System.out.println("Online Users:");
                    System.out.println(serverScanner.nextLine());
                    System.out.println();
                    confirmation();
                    break;
                case 2:
                    marketStart();
                    break;
                case 3:
                    // transferFunds
                    transferFunds();
                    confirmation();
                    break;
                case 4:
                    // transferFunds
                    addFunds();
                    confirmation();
                    break;
                case 5:
                    // transferFunds
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

    public void marketStart() throws IOException {
        String response;
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
            switch (val) {
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
                    addItemMarket();
                    confirmation();
                    break;
                case 3:
                    removeItemMarket();
                    confirmation();
                    break;
                case 4:
                    // Buy item
                    addItemUser();
                    confirmation();
                    break;
                case 5:
                    removeItemUser();
                    confirmation();
                    break;
                case 6:
                    running = false;
                    break;
            }
        }
    }

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
                int amount = Integer.parseInt(response);
                printWriter.println("AddResource-" + source + "-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }
        }
    }

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
                int amount = Integer.parseInt(response);

                printWriter.println("RemoveResource-Marketplace-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }

        }
    }

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
                int amount = Integer.parseInt(response);

                printWriter.println("RemoveResource-" + source + "-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }
        }
    }

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
                int amount = Integer.parseInt(response);

                printWriter.println("AddResource-Marketplace-" + resourceID + "-" + amount);
                System.out.println(serverScanner.nextLine());
            } else {
                System.out.println("Invalid entry.");
            }
        }
    }

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

    public void confirmation(){
        System.out.println("Press enter to continue.");
        scanner.nextLine();
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


}
