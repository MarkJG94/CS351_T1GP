import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleServer extends UnicastRemoteObject implements Runnable {

    ServerSocket serverSocket;

    //thread pool
    ExecutorService threadpool;

    private ArrayList<Resource> resources;
    private ArrayList<User> userList;
    private String filePath = new File("").getAbsolutePath();
    private String userFilePath = filePath + "/src/UserDetails.csv";
    private String resourceFilePath = filePath + "/src/MarketDetails.csv";
    private Marketplace marketPlace;
    private UserManager userManager;

    public ArrayList<User>getUserList(){
        return userList;
    }

    SimpleServer() throws IOException
    {
        serverSocket = new ServerSocket(11000);
        threadpool = Executors.newFixedThreadPool(20);

        resources = new ArrayList<>();
        userList = new ArrayList<>();

        importMarketDetails();
        importUserDetails();

        marketPlace = new Marketplace(resources);

    }
    
    @Override
    public void run() {
        try {
            System.out.println("server running");
            User test = new User( "Test", "Test",resources, 500 );
            userList.add( test );

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected " + client.toString() );
                //create socket handler and pass to thread pool
                threadpool.submit(new SocketHandler(client, userManager,marketPlace));
            }
        }catch (IOException e){
            e.printStackTrace();
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

        StringBuilder userHeader = new StringBuilder("Username,Password,Funds,Status,");

        for(Resource resource: resources){
            userHeader.append(resource.getName()).append(",");
        }

        String output = userHeader.deleteCharAt(userHeader.length() - 1).toString();
        bw.write(output);
        bw.newLine();

        for(User user: userList){
            StringBuilder output2 = new StringBuilder(user.getUsername() + "," + user.getPassword() + "," + user.funds + ",Offline");
            for (int i = 0; i < resources.size(); i++){
                output2.append(",").append(user.getResourceQuantity(i));
            }
            bw.write(output.toString());
            bw.newLine();

        }

        bw.close();
        fw.close();
    }

    public static void main(String[] args) {

        try {
            SimpleServer simpleServer = new SimpleServer();
            Thread thread = new Thread(simpleServer);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
