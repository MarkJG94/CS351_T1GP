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

    public int getNumericResponse(){
        System.out.print("  > ");
        String response = scanner.nextLine();
        if(isNumber(response)){
            return Integer.parseInt(response);
        } else if(response.equalsIgnoreCase("q")){
            return -2;
        }
        return -1;
    }

    private boolean isNumber(String s){
        if (s == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
