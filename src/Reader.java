import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Reader implements Runnable{

    Socket socket;

    public Reader(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while(true){
            try {
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                String fromServer = in.readLine();
                System.out.println(fromServer);
                if(fromServer.contains("IMPORTANT")){
                    System.out.println(fromServer);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
