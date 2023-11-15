
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SocketHandler implements Runnable{
    Socket socket;
    ArrayList<User> userList;
    SocketHandler(Socket socket, ArrayList<User> userList)
    {
        this.socket = socket;
        this.userList = userList;
    }

    @Override
    public void run() {
        try {

            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter printWriter = new PrintWriter( socket.getOutputStream(), true );
            String username = scanner.nextLine();
            
            boolean userExists = false;
            for (User user : userList)
            {
                if (user.getUsername().equals( username ))
                {
                    userExists = true;
                    break;
                }
            }
            
            if (userExists)
            {
                // Allow access
                printWriter.println("Login");
                
                String msg = "";
                while (true)
                {
                    msg = scanner.nextLine();
                    System.out.println(msg);
                }
            }
            else
            {
                printWriter.println("Denied");
                socket.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
