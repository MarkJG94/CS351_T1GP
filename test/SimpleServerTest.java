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
    public void givenValidMessageThenParseMessageReturnsTrue() {
        String message = "Buy-Marketplace-testuser1-5-2";

        assertEquals(true, simpleServer.parseClientMessage(message));
    }

    @Test
    public void givenInvalidMessageThenParseMessageReturnsFalse() {
        String message = "Buuy-Marketplace-testuser1-5-2";

        assertEquals(false, simpleServer.parseClientMessage(message));
    }

    @Test
    public void givenValidMessageThenParseClientMessageShowsUserInventory() {
        String message = "Inventory-testuser2";

        assertEquals(true, simpleServer.parseClientMessage(message));
        assertEquals(0, simpleServer.getMarketPlace().userList.get(1).userResources.get(0).getQuantity());
        assertEquals(3, simpleServer.getMarketPlace().userList.get(1).userResources.get(1).getQuantity());
        assertEquals(200, simpleServer.getMarketPlace().userList.get(1).userResources.get(2).getQuantity());
        assertEquals(10, simpleServer.getMarketPlace().userList.get(1).userResources.get(3).getQuantity());
        assertEquals(50, simpleServer.getMarketPlace().userList.get(1).userResources.get(4).getQuantity());
        assertEquals(600, simpleServer.getMarketPlace().userList.get(1).getFunds());
    }

    @Test
    public void givenValidMessageThenParseClientMessageShowsMarketPlaceInventory() {
        String message = "Inventory-Marketplace";

        assertEquals(true, simpleServer.parseClientMessage(message));
        assertEquals(50, simpleServer.getMarketPlace().marketResources.get(0).getQuantity());
        assertEquals(100, simpleServer.getMarketPlace().marketResources.get(1).getQuantity());
        assertEquals(30, simpleServer.getMarketPlace().marketResources.get(2).getQuantity());
        assertEquals(5, simpleServer.getMarketPlace().marketResources.get(3).getQuantity());
        assertEquals(25, simpleServer.getMarketPlace().marketResources.get(4).getQuantity());
    }

    @Test
    public void givenValidMessageThenParseClientMessagePerformsTransaction() {
        //testuser1 buying 5 stone, at 10 money each
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

    @Test
    public void givenInvalidMessageThenParseClientMessageReturnsFalseNoTransactionCompleted() {
        //testuser1 buying 5 stone, at 10 money each
        String message = "Buuy-Marketplace-testuser1-5-2";
        //Resources before
        assertEquals(100, simpleServer.getMarketPlace().marketResources.get(1).getQuantity());
        assertEquals(0, simpleServer.getMarketPlace().userList.get(0).userResources.get(1).getQuantity());
        assertEquals(1000, simpleServer.getMarketPlace().userList.get(0).getFunds());

        //Resources after
        assertEquals(false, simpleServer.parseClientMessage(message));
        assertEquals(100, simpleServer.getMarketPlace().marketResources.get(1).getQuantity());
        assertEquals(0, simpleServer.getMarketPlace().userList.get(0).userResources.get(1).getQuantity());
        assertEquals(1000, simpleServer.getMarketPlace().userList.get(0).getFunds());
    }

    @Test
    public void givenNotEnoughResourcesThenParseClientMessageReturnsFalseNoTransactionCompleted() {
        //testuser1 buying 10 gold, at 100 money each
        String message = "Buy-Marketplace-testuser1-10-4";
        //Resources before
        assertEquals(5, simpleServer.getMarketPlace().marketResources.get(3).getQuantity());
        assertEquals(20, simpleServer.getMarketPlace().userList.get(0).userResources.get(3).getQuantity());
        assertEquals(1000, simpleServer.getMarketPlace().userList.get(0).getFunds());

        //Resources after
        assertEquals(false, simpleServer.parseClientMessage(message));
        assertEquals(5, simpleServer.getMarketPlace().marketResources.get(3).getQuantity());
        assertEquals(20, simpleServer.getMarketPlace().userList.get(0).userResources.get(3).getQuantity());
        assertEquals(1000, simpleServer.getMarketPlace().userList.get(0).getFunds());
    }

    @Test
    public void givenNotEnoughFundsThenParseClientMessageReturnsFalseNoTransactionCompleted() {
        //testuser1 buying 25 silver, at 50 money each
        String message = "Buy-Marketplace-testuser1-25-5";
        //Resources before
        assertEquals(25, simpleServer.getMarketPlace().marketResources.get(4).getQuantity());
        assertEquals(30, simpleServer.getMarketPlace().userList.get(0).userResources.get(4).getQuantity());
        assertEquals(1000, simpleServer.getMarketPlace().userList.get(0).getFunds());

        //Resources after
        assertEquals(false, simpleServer.parseClientMessage(message));
        assertEquals(25, simpleServer.getMarketPlace().marketResources.get(4).getQuantity());
        assertEquals(30, simpleServer.getMarketPlace().userList.get(0).userResources.get(4).getQuantity());
        assertEquals(1000, simpleServer.getMarketPlace().userList.get(0).getFunds());
    }

}