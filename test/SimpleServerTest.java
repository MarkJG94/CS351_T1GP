import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class SimpleServerTest {

    private SimpleServer simpleServer;
    private ArrayList<User> userList;
    private ArrayList<Resource> resources;
    private String filePath = new File("").getAbsolutePath();
    private String userFilePath = filePath + "/src/UserDetails.csv";
    private String resourceFilePath = filePath + "/src/MarketDetails.csv";
    private Marketplace marketPlace;


    @Before
    public void setUp() throws Exception {
        simpleServer = new SimpleServer();
        userList = new ArrayList<>();
        resources = new ArrayList<>();
    }

    @Test
    public void parseClientMessage() {
        String message = "Buy-Marketplace-testuser1-5-2";

        assertEquals(true, simpleServer.parseClientMessage(message));
    }

    @Test
    public void givenValidMessageThenParseClientMessagePerformsTransaction() {
        //testuser1 buying 5 stone, at 10 gold each
        String message = "Buy-Marketplace-testuser1-5-2";
        //Resources before
        assertEquals(100, simpleServer.getMarketPlace().marketResources.get(1).getQuantity());
        assertEquals(0, simpleServer.getMarketPlace().userList.get(0).userResources.get(1).getQuantity());
        assertEquals(1000, simpleServer.getMarketPlace().userList.get(0).getFunds());

        //Resources after
        assertEquals(true, simpleServer.parseClientMessage(message));
        assertEquals(95, simpleServer.getMarketPlace().marketResources.get(1).getQuantity());
        assertEquals(5, simpleServer.getMarketPlace().userList.get(0).userResources.get(1).getQuantity());
        assertEquals(950, simpleServer.getMarketPlace().userList.get(0).getFunds());
    }


}