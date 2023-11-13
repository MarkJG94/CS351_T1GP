import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        Marketplace marketplace = new Marketplace();

        importMarketDetails();
        importUserDetails();
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

    private void importMarketDetails(){
        List<List<String>> records = new ArrayList<>();
        try (
                BufferedReader br = new BufferedReader(
                        new FileReader("C:\\Users\\pfb21179\\OneDrive - University of Strathclyde\\Coursework\\CS351 Programming\\CS351_T1GP\\src\\MarketDetails.csv")
                )
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < records.size();i++){
            resources.add(new Resource(i + 1, Integer.parseInt(records.get(i).get(2)), Integer.parseInt(records.get(i).get(1)), records.get(i).get(0)));
        }
    }

    private void importUserDetails(){
        List<List<String>> records = new ArrayList<>();
        try (
                BufferedReader br = new BufferedReader(
                        new FileReader("C:\\Users\\pfb21179\\OneDrive - University of Strathclyde\\Coursework\\CS351 Programming\\CS351_T1GP\\src\\UserDetails.csv")
                )
        ) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(List<String> user : records){
            ArrayList<Resource> userResources = new ArrayList<>();
            int start = 4;

            for(int i = 0; i < resources.size();i++){
                userResources.add(new Resource(resources.get(i).getId(),resources.get(i).getCost(), Integer.parseInt(user.get(start)), resources.get(i).getName()));
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
