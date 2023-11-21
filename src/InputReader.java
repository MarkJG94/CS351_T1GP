import java.util.Scanner;

public class InputReader {

    Scanner scanner;

    InputReader(){
        scanner = new Scanner(System.in);
    }

    public String getResponse(){
        System.out.print("  > ");
        return scanner.nextLine();
    }
}
