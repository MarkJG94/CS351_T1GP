import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.lang.Thread.sleep;

/*
    This class is the main server that can be created in two modes;
    1. In "server-only" mode to run the server and receive connections without the administrator menu
    2. In "server + admin" mode to run a separate thread for the administrator menu(s)

    This class imports the market resource list and user list on creation and overwrites files on close with any changes that have occurred since launch
 */
public class Server implements Runnable {

    public ServerSocket serverSocket;

    //thread pool
    private final ExecutorService threadpool;
    private final ArrayList<Resource> resources;
    private final ArrayList<User> userList;

    // Gets the dynamic file path to the current location
    private final String filePath = new File("").getAbsolutePath();

    // Appends the folder and file name for the user details CSV
    private final String userFilePath = filePath + "/data/UserDetails.csv";

    // Appends the folder and file name for the market resource CSV
    private final String resourceFilePath = filePath + "/data/MarketDetails.csv";
    private final Marketplace marketPlace;
    private UserManager userManager;
    private final ArrayList<Socket> clients;
    Administrator administrator;
    Thread adminThread;
    Thread server;

    // Used to specify if the server will be in server-only mode (1) or server + admin (0)
    int runMode;

    // Constructor that will instantiate the server and set the run mode to the provided value
    Server(int i) throws IOException {
        runMode = i;
        serverSocket = new ServerSocket(11000);
        threadpool = Executors.newFixedThreadPool(20);

        resources = new ArrayList<>();
        userList = new ArrayList<>();

        clients = new ArrayList<>();

        importMarketDetails();
        importUserDetails();

        marketPlace = new Marketplace(resources);
    }

    // Constructor that calls the main constructor and provides a default value for the runMode
    Server() throws IOException
    {
        this(0);
    }

    // Run method that will verify the server is running and if the run mode is set to 0, instantiate the administrator object and assign it to a separate thread.
    @Override
    public void run() {
        try {
            System.out.println("server running");
            System.out.println();
            if(runMode == 0){
                administrator = new Administrator("42b0307fc70d04e46e2c189eb011259c94998921fc6b394448f4a2705453cf698f749cb733226d80f40786cb12c857122d253a5e325cdbe91ad325e75b129ab8ba88008c10a5160035e21bc92993c3647fc10fb1307049d14a51789bdca7e436d5fee2b3b4dc5c3b7e611add83edf71284764d775bd049d286c23760765263f965559f20b77b794d6365678be2ae47f8572a4fd253cef295e0b1e4412245bb63");
                adminThread = new Thread(administrator);
                threadpool.submit(adminThread);
            }
            server = new Thread(this::runServer);
            server.start();

            // Main loop that will loop every second to see if users are still connected, to verify if the administrator is still running
            while(true){
                sleep(1000);
                if(clients.size() > 0) {
                    if (clients.get(0).isClosed()) {
                        break;
                    }
                    for(int j = 0; j < clients.size(); j++){
                        // Skips the first client (i.e. the administrator "client")
                        if(j > 0) {
                            // Sends a "heartbeat" to each client to verify if they are still connected to the server.
                            Socket c = clients.get(j);
                            PrintWriter pw = new PrintWriter(c.getOutputStream(), true);
                            pw.println("heartbeat");

                            // If the print writer returns an error, search the socket/user map to find the associated user and set their status to offline
                            // Then deletes the client and removes them from the socket hashmap
                            if (pw.checkError()) {
                                User offlineUser = null;
                                for (Map.Entry<User, Socket> entry : userManager.socketUserMap.entrySet()) {
                                    if (entry.getValue() == c) {
                                        offlineUser = entry.getKey(); 
                                        entry.getKey().setOffline();
                                    }
                                }
                                clients.remove(c);
                                userManager.socketUserMap.remove(offlineUser);
                                c.close();
                            }
                        }
                    }
                }
            }

            // If the loop ends, the administrator has indicated to shut down the server

            // Loop through each socket and send a message to notify of the impending shutdown with the "IMPORTANT" keyword
            for(Socket s : clients){
                if(!s.isClosed()){
                    PrintWriter pw =  new PrintWriter(s.getOutputStream(), true);
                    pw.println("IMPORTANTServer is shutting in 10 seconds.");

                }
            }

            // Prints a 10 second countdown to the server
            for(int i = 0; i < 10; i++) {
                if ((10 - i) == 10){
                    System.out.print(10 - i + "   ");
                } else if((10 - i) < 6){
                    System.out.print(10 - i + "   ");
                }

                // Wait 1 second before continuing the loop
                sleep(1000);
            }

            // After 10 seconds has elapsed, print an IMPORTANT message to each client and notify the client that the server is now offline
            for(Socket s : clients){
                if(!s.isClosed()){
                    PrintWriter pw =  new PrintWriter(s.getOutputStream(), true);
                    pw.println("IMPORTANTServer is now Offline");
                    pw.println("Server Offline");
                }
            }

            // Sleep for a further 1 second before quiting the server thread, saving the marketplace resource list and user list to CSV and close the application
            sleep(1000);

            quit(0);
            saveMarketFile();
            saveUserFile();

            System.exit(0);
        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            // If the server throws an interrupted exception, the server has ended abruptly in which case call the quit method with a -1 value
            quit(-1);
        }
    }

    // Method to close the admin thread, close the server thread and shutdown the threadpool
    private void quit(int i){
        if(runMode == 0) {
            adminThread.interrupt();
        }
        server.interrupt();
        threadpool.shutdown();
        if(i == 0) {
            System.out.print("Goodbye");
        }
    }

    // Method that will await a new client to connect and automatically accept the connection, create a new sockethandler object with the new client
    private void runServer() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                clients.add(client);
                //create socket handler and pass to thread pool
                threadpool.submit(new SocketHandler(client, userManager,marketPlace));
            } catch (IOException e) {
                quit(-1);
            }
        }
    }

    // Method to import the marketplace resource list from file, or create a default file if one does not already exist
    private void importMarketDetails() throws IOException {

        List<List<String>> records = new ArrayList<>();
        try (
                BufferedReader br = new BufferedReader(
                        new FileReader(resourceFilePath)
                )
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
            for(int i = 0; i < records.size();i++){
                resources.add(new Resource(i + 1, Integer.parseInt(records.get(i).get(2)), Integer.parseInt(records.get(i).get(1)), records.get(i).get(0),Integer.parseInt(records.get(i).get(3))));
            }
        } catch (IOException e) {

            File file = new File(resourceFilePath);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            resources.add(new Resource(1, 10, 50, "Wood", 8));
            resources.add(new Resource(2, 10, 100, "Stone", 8));
            resources.add(new Resource(3, 30, 30, "Iron", 24));
            resources.add(new Resource(4, 100, 5, "Gold", 80));
            resources.add(new Resource(5, 50, 20, "Silver", 40));

            for (int i = 0; i < resources.size(); i++) {
                String resourceString = resources.get(i).getName() + "," + resources.get(i).getQuantity() + "," + resources.get(i).getCost() + "," + resources.get(i).getValue();
                bw.write(resourceString);
                if(i != resources.size() - 1) {bw.newLine();}
            }

            bw.close();
            fw.close();
        }
    }

    // Method to import the user list from file, or create a default file if one does not already exist
    private void importUserDetails() throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (
                BufferedReader br = new BufferedReader(
                        new FileReader(userFilePath)
                )
        ) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            File file = new File(userFilePath);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            StringBuilder userHeader = new StringBuilder("Username,Password,Funds,Status,");

            for(Resource resource: resources){
                userHeader.append(resource.getName()).append(",");
            }

            String output = userHeader.deleteCharAt(userHeader.length() - 1).toString();
            bw.write(output);
            bw.newLine();

            StringBuilder testUser = new StringBuilder("testuser,Test,0,Offline,");

            for(Resource resource: resources){
                testUser.append("0,");
            }

            output = testUser.deleteCharAt(testUser.length() - 1).toString();
            bw.write(output);
            bw.newLine();

            bw.close();
            fw.close();
        }

        for(List<String> user : records){
            ArrayList<Resource> userResources = new ArrayList<>();
            int start = 4;

            for(int i = 0; i < resources.size();i++){
                userResources.add(new Resource(resources.get(i).getId(),resources.get(i).getCost(), Integer.parseInt(user.get(start)), resources.get(i).getName(),resources.get(i).getValue()));
                start++;
            }

            userList.add(
                    new User(
                            user.get(0),
                            user.get(1),
                            userResources,
                            Integer.parseInt(user.get(2))
                    )
            );
            userManager = new UserManager(userList);
        }
    }

    // Method to save the current marketplace resource list to file, overwriting the file that currently exists
    private void saveMarketFile() throws IOException{
        File file = new File(resourceFilePath);
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        ArrayList<Resource> resources = marketPlace.getMarketResources();
        for (int i = 0; i < resources.size(); i++) {
            String resourceString = resources.get(i).getName() + "," + resources.get(i).getQuantity() + "," + resources.get(i).getCost() + "," + resources.get(i).getValue();
            bw.write(resourceString);
            if(i != resources.size() - 1) {bw.newLine();}
        }

        bw.close();
        fw.close();
    }

    // Method to save the current user list to file, overwriting the file that currently exists
    private void saveUserFile() throws IOException {
        File file = new File(userFilePath);
        FileWriter fw = new FileWriter(file, false);
        BufferedWriter bw = new BufferedWriter(fw);
        ArrayList<Resource> resources = marketPlace.getMarketResources();
        StringBuilder userHeader = new StringBuilder();
        userHeader.append("Username,Password,Funds,Status");
        for(Resource resource: resources){
            userHeader.append(",").append(resource.getName());
        }

        String output = userHeader.toString();
        bw.write(output);
        bw.newLine();
        ArrayList<User> userList = userManager.getUserList();
        for(User user: userList){
            StringBuilder output2 = new StringBuilder(user.getUsername() + "," + user.getPassword() + "," + user.getFunds() + ",Offline");
            for (int i = 0; i < resources.size(); i++){
                output2.append(",").append(user.getResourceQuantity(i+1));
            }
            bw.write(output2.toString());
            bw.newLine();

        }

        bw.close();
        fw.close();
    }

    // Driver method that will instantiate the server object and assign it to a thread
    public static void main(String[] args) {

        try {
            Server server = new Server();
            Thread thread = new Thread(server);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
