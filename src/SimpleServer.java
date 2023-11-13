import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
        userList = new ArrayList<>();
    }
    
    
    
    @Override
    public void run() {
        try {
            System.out.println("server running");
            User test = new User( "Test", resources, 500 );
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
