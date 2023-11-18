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

        marketPlace = new Marketplace(userList, resources);
    }
    
    public Marketplace getMarketPlace(){
        return marketPlace;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("server running");
            User test = new User( "Test", "Test",resources, 500 );
            userList.add( test );


            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected");
                //create socket handler and pass to thread pool
                threadpool.submit(new SocketHandler(client, userList));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public ArrayList<String> marketMenu(){
        ArrayList<String> options = new ArrayList<String>();
        options.add("Marketplace Menu");
        options.add("Please select an option from the list below;");
        options.add("\t 1. View listings");
        options.add("\t 2. Buy Items");
        options.add("\t 3. Sell Items");
        options.add("\t 4. Main Menu");
        
        return options;
    }

    public void importMarketDetails() throws IOException {

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

    public void importUserDetails() throws IOException {
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



    public boolean parseClientMessage(String message){
        String array[] = message.split("-");
        String command = array[0];
        if (command.equals("Inventory")){
            String source = array[1];
            if (source.equals("Marketplace")){
                ArrayList<Resource> marketResources = marketPlace.getMarketInventory();
                printInventory(marketResources, true, null);
                return true;
            } else if (marketPlace.userExists(source)){
                ArrayList<Resource> userResources = marketPlace.getUserInventory(source);
                printInventory(userResources, false, source);
                return true;
            } else {
                return false;
            }
        }
        else if(command.equals("Buy")){
            String source = array[1];
            //Can only buy from Marketplace
            if (source.equals("Marketplace")){
                //If user exists
                if (marketPlace.userExists(array[2])){
                    String dest = array[2];
                    Integer amount = Integer.parseInt(array[3]);
                    Integer itemId = Integer.parseInt(array[4]);
                    int resourceCost = marketPlace.getResourceDetails(itemId).getCost();
                    int total = amount * resourceCost;
                    //If user has enough money, perform transaction
                    if(marketPlace.getFunds(dest) >= total){
                        marketPlace.removeResourceFromMarket(itemId, amount);
                        marketPlace.addResourceToUser(itemId, amount, dest);
                        marketPlace.deductFunds(dest, total);
                        return true;
                    } else {
                        //User did not have enough money
                        return false;
                    }
                }
                //User did not exist
                else {
                    return false;
                }

                //Source was not Marketplace
            } else {
                return false;
            }
        }
        else if (command.equals("Sell")){

        }
        else if (command.equals("Transfer")){

        } else {
            array[0] = "-1";
        }

        return false;
    }

    private void printInventory(ArrayList<Resource> inventory, boolean market, String username){
        if(market){
            System.out.println("*** Market Inventory ***");
            System.out.println("\tResource\tAmount\tCost to buy");
            for (Resource resource: inventory) {
                System.out.println("\t" + resource.getName() +"\t" + resource.getQuantity() + resource.getCost());
            }
        } else {
            System.out.println("*** " + username + "'s Inventory ***");
            System.out.println("\tResource\tAmount\tValue if sold");
            for (Resource resource: inventory) {
                System.out.println("\t" + resource.getName() +"\t" + resource.getQuantity() + resource.getValue());
            }
        }
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
