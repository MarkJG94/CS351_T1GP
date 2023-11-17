
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

    private User checkUsername(String username){
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {

            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter printWriter = new PrintWriter( socket.getOutputStream(), true );
            String username;
            User user;

            boolean userExists = false;

            while(true) {
                username = scanner.nextLine();
                user = checkUsername(username);
                if (user == null) {
                    printWriter.println("Username");
                } else {
                    break;
                }
            }
            while(!userExists) {
                printWriter.println("Password");
                String password = scanner.nextLine();
                System.out.println(password);
                if (user.getPassword().equals(password)) {
                    userExists = true;
                    printWriter.println("Login");
                } else {
                    printWriter.println("Password");
                }
            }
            
            if (userExists)
            {
                // Allow access
                printWriter.println("Login");
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
