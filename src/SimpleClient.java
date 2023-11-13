
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SimpleClient {

    SimpleClient() {

    }

    public void runClient() throws IOException {
        Socket socket = new Socket("127.0.0.1", 11000);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        
        printWriter.println(username);
        
        Scanner serverScanner = new Scanner(socket.getInputStream());
        String response = serverScanner.nextLine();
        System.out.println(response);
        
        String msg = "";
        while (!msg.equals("STOP")){
            msg = scanner.nextLine();
            printWriter.println(msg);
        }
    }

    public static void main(String[] args) {
        SimpleClient simpleClient = new SimpleClient();
        try {
            simpleClient.runClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
