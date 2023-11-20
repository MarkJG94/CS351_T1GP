import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Administrator implements Runnable {

    Socket socket;
    PrintWriter printWriter;
    Scanner serverScanner;
    String username = "admin";
    String pw = "42b0307fc70d04e46e2c189eb011259c94998921fc6b394448f4a2705453cf698f749cb733226d80f40786cb12c857122d253a5e325cdbe91ad325e75b129ab8ba88008c10a5160035e21bc92993c3647fc10fb1307049d14a51789bdca7e436d5fee2b3b4dc5c3b7e611add83edf71284764d775bd049d286c23760765263f965559f20b77b794d6365678be2ae47f8572a4fd253cef295e0b1e4412245bb63";
    Administrator() throws IOException {
        socket = new Socket("127.0.0.1", 11000);
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        serverScanner = new Scanner(socket.getInputStream());
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
        options.add("\t 2. Add Items");
        options.add("\t 3. Remove Items");
        options.add("\t 4. Main Menu");
        for(String s : options){
            System.out.println(s);
        }
    }

    public void start() throws IOException {
        String response;
        Scanner scanner = new Scanner( System.in );
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
                response = scanner.nextLine();
                if ((Integer.parseInt(response) > 0) && ((Integer.parseInt(response) < 7)))
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid entry");
                }
            }
            switch (Integer.parseInt(response)) {
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
        int response;

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
                    addItem();
                    confirmation();
                    break;
                case 3:
                    removeItem();
                    confirmation();
                    break;
                case 4:
                    running = false;
                    break;
            }
        }
    }

    private void removeFunds() {
    }

    private void addFunds() {

    }

    private void addItem() {
    }

    private void removeItem() {
        
    }

    private void transferFunds() throws IOException {
        Scanner scanner = new Scanner( System.in );

        System.out.println("Enter the username you would like to transfer from: ");
        String source = scanner.nextLine();

        System.out.println("Enter the username you would like to transfer to: ");
        String destination = scanner.nextLine();
        
        System.out.println("Amount you want to transfer: ");
        int amount = scanner.nextInt();

        printWriter.println("Transfer-" + source + "-" + destination + "-" + amount);
        System.out.println(serverScanner.nextLine());

    }

    public void confirmation(){
        Scanner scanner = new Scanner(System.in);
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
