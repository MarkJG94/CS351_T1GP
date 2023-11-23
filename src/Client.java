import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.util.*;

public class Client extends InputReader{
    private final Socket socket;
    private String username;
    InputReader inputReader;
    private final Stack<String> messageQueue;
    Thread operator;
    PrintWriter printWriter;
    Client() throws IOException {
        socket = new Socket("127.0.0.1", 11000);
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        messageQueue = new Stack<>();
        inputReader = new InputReader();
    }

    public void runClient() throws IOException, NotBoundException {
        String response;
        int val;
        startServerResponseQueue();

        while (true) {
            System.out.println("Select an option:");
            System.out.println("    1. Sign-in");
            System.out.println("    2. Create Account");
            System.out.println("    3. Exit");

            val = inputReader.getNumericResponse();
            if (val > 0 && val < 4) {
                break;
            } else {
                System.out.println("Invalid Entry, please try again");
                System.out.println();
            }
        }
        if(val == 3) {
            exit();
        } else
        if(val == 2) {
            printWriter.println("NewAccount");
            while (true) {
                System.out.println("Please enter your username:");
                username = inputReader.getResponse();
                printWriter.println(username);
                if (retrieveResponse().equals("password")) {
                    break;
                } else {
                    System.out.println("This username already exists.");
                }
            }
            while (true) {
                System.out.println("Please enter your password:");
                String password = inputReader.getResponse();
                printWriter.println(password);
                if (password.contains(",")) {
                    System.out.println("Password cannot contain ','. Please try again.");
                } else if (password.equalsIgnoreCase("marketplace")) {
                    System.out.println("Invalid password. Please try again.");
                } else if (retrieveResponse().equals("success")) {
                    System.out.println("User successfully created");
                    break;
                } else {
                    System.out.println("Failed to create user account!");
                    break;
                }
            }
        } else {
            printWriter.println("Username");
        }

        while (true) {
            System.out.println("Enter your username: ");
            username = inputReader.getResponse();

            sendAnswer(username);

            response = retrieveResponse();
            if (response.equals("Password")) {
                break;
            } else {
                System.out.println("Username not found. Please try again");
            }
        }

        int passwordAttempts = 0;
        while (true) {

            if (passwordAttempts > 4) {
                break;
            } else {
                System.out.println("Enter your password: ");
            }

            String password = inputReader.getResponse();
            sendAnswer(password);

            response = retrieveResponse();

            if (response.equals("Login")) {
                break;
            } else {
                System.out.println("Invalid Password! " + (3 - passwordAttempts) + " attempts remaining.");
                passwordAttempts++;
            }
        }

        if (response.equals("Login")) {
            start();
        }
        socket.close();
    }

    private void startServerResponseQueue() throws IOException {
        Scanner serverScanner = new Scanner(socket.getInputStream());
        operator = new Thread() {
            public void run() {
                while (!operator.isInterrupted() && socket.isConnected()) {
                    String response;
                    try{response = serverScanner.nextLine();}
                    catch(NoSuchElementException e){
                        messageQueue.add("Server Offline");
                        break;
                    }

                    if(response.equals("Logging out")) {
                        messageQueue.add(response);
                        break;
                    } else if(response.equals("Server Offline")){
                        System.exit(0);
                    } else if(response.contains("IMPORTANT")) {
                        System.out.println();
                        System.out.println(response.replace("IMPORTANT","[SERVER]: "));
                        System.out.print("  > ");
                    } else if(response.equals("heartbeat")) {
                        continue;
                    } else {
                        messageQueue.add(response);
                    }
                }
            }
        };
        operator.start();
    }

    private void endServerResponseQueue(){
        operator.interrupt();
    }

    public void start() throws IOException {
        String response;
        int val;
        boolean running = true;
        while (running) {
            mainMenu();
            while (true) {
                System.out.println("Please enter a number (1-5): ");
                val = inputReader.getNumericResponse();
                if (val > 0 && (val < 6))
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
                    // View inventory
                    sendAnswer("Inventory-" + username);
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(retrieveResponse().split("`")));
                    for(String s : data){
                        System.out.println(s);
                    }
                    System.out.println();
                    confirmation();
                    break;
                case 2:
                    // view logged in users
                    sendAnswer("Users-" + username);
                    response = retrieveResponse();
                    System.out.println("Online Users:");
                    System.out.println(response);
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
                    running = quitConfirmation();
                    break;
            }
        }
    }

    public void marketStart() throws IOException {
        int val;
        String response;
        boolean running = true;
        while (running) {
            marketMenu();
            while (true) {
                System.out.println("Please enter a number (1-4): ");
                val = inputReader.getNumericResponse();
                if ((val > 0) && (val < 5))
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
                    sendAnswer("Inventory-Marketplace");
                    response = retrieveResponse();
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));
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
        System.out.println("Enter the username you would like to transfer to: ");
        String response;
        int val;
        String input = inputReader.getResponse();

        System.out.println("Amount you want to transfer: ");
        val = inputReader.getNumericResponse();

        int amount = val;
        if (amount > 0) {
            sendAnswer("Transfer-" + username + "-" + input + "-" + amount);
            System.out.println(retrieveResponse());
        } else {
            System.out.println("Invalid amount.");
        }
    }

    private void sellItem() throws IOException {
        sendAnswer("Inventory-Marketplace-" + username);
        String response = retrieveResponse();
        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));

        int val;
        boolean loop = true;
        int resourceID = 0, quantity = 0;

        while (loop) {
            System.out.println("Which item would you like to sell? (enter Q to exit)");
            int i = 1;
            for(String s : data){
                System.out.print(s.split(":")[0] + " (" + i + ")    ");
                i++;
            }
            System.out.println();
            val = inputReader.getNumericResponse();
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
        while (loop) {
            System.out.println("How many would you like to sell? (enter Q to exit)");
            val = inputReader.getNumericResponse();
            if (val == -2) {
                loop = false;
                break;
            } else if (val > 0) {
                quantity = val;
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        if(loop) {
            sendAnswer("Sell-Marketplace-" + username + "-" + resourceID + "-" + quantity);
            System.out.println(retrieveResponse());
        }

    }

    private void buyItem() throws IOException {
        sendAnswer("Inventory-Marketplace-" + username);
        String response = retrieveResponse();
        ArrayList<String> data = new ArrayList<>(Arrays.asList(response.split("`")));
        int val;
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
            val = inputReader.getNumericResponse();
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
        while (loop) {
            System.out.println("How many would you like to buy? (enter Q to exit)");
            val = inputReader.getNumericResponse();
            if (val == -2) {
                loop = false;
                break;
            } else if (val > 0) {
                quantity = val;
                break;
            } else {
                System.out.println("Invalid entry. Try again.");
            }
        }
        if(loop) {
            sendAnswer("Buy-Marketplace-" + username + "-" + resourceID + "-" + quantity);
            System.out.println(retrieveResponse());
        }

    }

    private boolean quitConfirmation(){
        System.out.println();

        while(true){
            System.out.println("Are you sure you want to quit? (Y/N)");
            String response = inputReader.getResponse();

            switch(response.toUpperCase()) {
                case "Y":
                case "YES:":
                    quit();
                    return false;
                case "N":
                case "NO":
                    return true;
                default:
                    System.out.println("Invalid entry, please try again.");
                    break;
            }
        }
    }

    private void quit(){
        sendAnswer("Quit-" + username);
        System.out.println(retrieveResponse());
        exit();
    }

    private void exit(){
        endServerResponseQueue();
        System.exit(0);
    }

    private void confirmation(){
        System.out.println("Press enter to continue.");
        scanner.nextLine();
    }

    private void sendAnswer(String s){
        printWriter.println(s);
    }

    private String retrieveResponse(){
        while(messageQueue.size() == 0){
        }
        return messageQueue.pop();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        try {
            client.runClient();
        } catch ( IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
