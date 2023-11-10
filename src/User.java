import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

//get online users
public class User implements Market {

    ArrayList<Resource> userResources;
    String username;
    int funds;

    public User(String username, ArrayList<Resource> userResources){
        this.username = username.toLowerCase(Locale.ROOT);
        this.userResources = userResources;
        funds = 1000;
    }

    public User(String username, ArrayList<Resource> userResources, int funds){
        this.username = username;
        this.userResources = userResources;
        this.funds = funds;
    }

    public ArrayList<String> getUserInventory(){
        //reads in file and prints all users inventory
        try {
            BufferedReader  bufferedReader = new BufferedReader(new FileReader("UserDetails.txt"));
            ArrayList<String> userInventory = new ArrayList<>();

            String line = bufferedReader.readLine();
            while (line !=null){
                userInventory.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            System.out.println(userInventory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getUserInventory();

    }

    @Override
    public ArrayList<String> getUserInventory(String username) {
        //search for username then print users inventory
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    System.out.println(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getUserInventory();

    }

    @Override
    //change parameters?
    public int getResourceQuantity() {
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;
                    System.out.println(wood);
                    System.out.println(stone);
                    System.out.println(iron);
                    System.out.println(gold);
                    System.out.println(silver);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int addFunds(String username, int amount) {
        //read file, add funds, delete row in file and then write back to file
        //what are we adding?
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        funds=funds+amount;
        return funds;

    }


    @Override
    public int deductFunds(String username, int amount) {
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        funds = funds - amount;
        return funds;

    }


    @Override
    public boolean validateCurrency(int amount) {
        if (funds > 0){
            return true;
        }
        return false;
    }

    public int getFunds(){
        return funds;
    }

    @Override
    public int getFunds(String username) {
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return funds;
    }

    public String getUsername(){
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return username;

    }


    public boolean transferFunds(String source, String destination, int amount){
        return true;
    }

    @Override
    public boolean userExists(String username) {
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addItem(String itemID, int quantity) {
        //read file, add to wood or iron ... quantity
        boolean added = false;
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                    if(itemID == "wood"){
                        wood = quantity+wood;
                        added=true;
                        return added;
                    }else if (itemID=="stone"){
                        stone = quantity+stone;
                        added=true;
                        return added;
                    }else if (itemID=="iron"){
                        stone = quantity+iron;
                        added=true;
                        return added;
                    }else if (itemID=="gold"){
                        stone = quantity+gold;
                        added=true;
                        return added;
                    }else if (itemID=="silver"){
                        stone = quantity+silver;
                        added=true;
                        return added;
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //doesnt write back to file yet
        return added;
    }

    @Override
    public boolean removeItem(String itemID, int quantity) {
        //read file, add to wood or iron ... quantity
        boolean added = false;
        try {
            Scanner scanner = new Scanner(new File("UserDetails.txt"));
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.contains(username)){
                    String[] parts = line.split(",");
                    System.out.println(line);
                    username = parts[0].trim();
                    String password = parts[1].trim();
                    int currency = Integer.parseInt(parts[2].trim());
                    String status = parts[3].trim();
                    String accountType = parts[4].trim();
                    int wood = Integer.parseInt(parts[5].trim());
                    int stone = Integer.parseInt(parts[6].trim());
                    int iron = Integer.parseInt(parts[7].trim());
                    int gold = Integer.parseInt(parts[8].trim());
                    int silver = Integer.parseInt(parts[9].trim());
                    funds=funds+currency;

                    if(itemID == "wood"){
                        wood = quantity-wood;
                        added=true;
                        return added;
                    }else if (itemID=="stone"){
                        stone = quantity-stone;
                        added=true;
                        return added;
                    }else if (itemID=="iron"){
                        stone = quantity-iron;
                        added=true;
                        return added;
                    }else if (itemID=="gold"){
                        stone = quantity-gold;
                        added=true;
                        return added;
                    }else if (itemID=="silver"){
                        stone = quantity-silver;
                        added=true;
                        return added;
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //doesnt write back to file yet
        return added;
    }

    public boolean buyItem(ArrayList<Resource> userResources, int funds){
        return true;
        //username, resource, fund
    }

    public boolean sellItem(ArrayList<Resource> userResources, int funds){
        return true;
    }

    public boolean notifyUser(String username, int quantity, String resourceName){

        System.out.println("Your "+ resourceName + "is now" + quantity);
        return true;
    }



}
