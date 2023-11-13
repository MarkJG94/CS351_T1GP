import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleServer implements Runnable{

    ServerSocket serverSocket;

    //thread pool
    ExecutorService threadpool;

    private ArrayList<Resource> resources;
    private ArrayList<User> userList;
    private Marketplace marketPlace;

    public ArrayList<User>getUserList(){
        return userList;
    }

    SimpleServer() throws IOException {
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
