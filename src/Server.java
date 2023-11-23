import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class Server implements Runnable {

    ServerSocket serverSocket;

    //thread pool
    ExecutorService threadpool;

    private ArrayList<Resource> resources;
    private ArrayList<User> userList;
    private String filePath = new File("").getAbsolutePath();
    private String userFilePath = filePath + "/data/UserDetails.csv";
    private String resourceFilePath = filePath + "/data/MarketDetails.csv";
    private Marketplace marketPlace;
    private UserManager userManager;
    private ArrayList<Socket> clients;

    Administrator administrator;
    Thread adminThread;
    Thread server;
    int runMode;

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
    Server() throws IOException
    {
        this(0);
    }
    
    @Override
    public void run() {
        try {
            System.out.println("server running");
            System.out.println();
            if(runMode == 0){
                administrator = new Administrator();
                adminThread = new Thread(administrator);
                threadpool.submit(adminThread);
            }
            server = new Thread(this::runServer);
            server.start();
            while(true){
                sleep(1000);
                if(clients.size() > 0) {
                    if (clients.get(0).isClosed()) {
                        break;
                    }
                    for(int j = 0; j < clients.size(); j++){
                        if(j > 0) {
                            Socket c = clients.get(j);
                            PrintWriter pw = new PrintWriter(c.getOutputStream(), true);
                            pw.println("heartbeat");
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
                            }
                        }
                    }
                }
            }
            for(Socket s : clients){
                if(!s.isClosed()){
                    PrintWriter pw =  new PrintWriter(s.getOutputStream(), true);
                    pw.println("IMPORTANTServer is shutting in 10 seconds.");

                }
            }
            for(int i = 0; i < 10; i++) {
                System.out.print(10 - i + "   ");
                sleep(1000);
            }
            for(Socket s : clients){
                if(!s.isClosed()){
                    PrintWriter pw =  new PrintWriter(s.getOutputStream(), true);
                    pw.println("IMPORTANTServer is now Offline");
                    pw.println("Server Offline");
                }
            }
            sleep(1000);

            quit(0);
            saveMarketFile();
            saveUserFile();

            System.exit(0);
        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            quit(-1);
        }
    }

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
