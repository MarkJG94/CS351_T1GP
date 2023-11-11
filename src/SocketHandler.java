import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SocketHandler implements Runnable{
    Socket socket;
    SocketHandler(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
        try {

            Scanner scanner = new Scanner(socket.getInputStream());
            String msg = "";
            while (true) {
                msg = scanner.nextLine();
                System.out.println(msg);

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
