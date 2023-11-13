import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportTest {

    private static final String COMMA_DELIMITER = ",";
    private ArrayList<Resource> resources;
    private ArrayList<User> userList;
    private Marketplace marketPlace;



    public ImportTest () throws FileNotFoundException {



    }

    public static void main(String[] args) throws FileNotFoundException {
        new ImportTest();
    }
}

