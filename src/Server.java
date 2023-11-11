import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ArrayList<Resource> resources;
    private ArrayList<User> userList;
    private Marketplace marketPlace;

    public ArrayList<User>getUserList(){
        return userList;
    }

    public static void main(String[] args) throws IOException {
        try{
            ServerSocket serverSocket = new ServerSocket(5001);
            System.out.println("Server Listening on port 5001...");

            while (true) {
                Socket connectionSocket = serverSocket.accept();
                new Thread(() -> {
                    // Code to process client calculation requests
                    // ...
                }).start();
            }

        } catch (IOException e){
            System.out.println(e.toString());
        }
    }

    private void menu_options(){
        System.out.println("Please select an option below;");
        System.out.println("\t 1. ");
    }
}
