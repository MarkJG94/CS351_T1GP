import java.util.Scanner;

/*
    The class is used to provide a uniform prompt for input and return the input
 */
public class InputReader {

    Scanner scanner;

    InputReader(){
        scanner = new Scanner(System.in);
    }

    // Prompts for user input and return exactly as the user provides
    public String getResponse(){
        System.out.print("  > ");
        return scanner.nextLine();
    }

    // Prompts for numeric user input, and provides a -1 value if the input is an invalid character, or -2 if the user chooses to quit
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

    // Basic method to verify if a string is an integer or not
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
