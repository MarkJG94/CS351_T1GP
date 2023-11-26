import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Timeout;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocketHandlerTest
{
    Server server = new Server(1);
    Thread serverThread = new Thread( server);
    Client client1 = new Client();
    Client client2 = new Client();
    SocketHandler socketHandler;
    Socket socket = new Socket();
    UserManager userManager;
    Marketplace marketplace;
    
    PrintWriter printWriter;
    
    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    Resource wood = new Resource(1, 1, 1000, "wood", 1);
    Resource iron = new Resource(2, 2, 100, "iron", 1);
    Resource steel = new Resource(3, 5, 10, "steel",4);
    Resource silver = new Resource(4, 10, 5, "silver",8);
    Resource gold = new Resource(5, 100, 1, "gold",80);
    
    ArrayList<Resource> marketResources = new ArrayList<Resource>();
    Resource wood1 = new Resource(1, 1, 10000, "wood", 1);
    Resource iron1 = new Resource(2, 2, 1000, "iron", 1);
    Resource steel1 = new Resource(3, 5, 100, "steel",4);
    Resource silver1 = new Resource(4, 10, 10, "silver",8);
    Resource gold1 = new Resource(5, 100, 1, "gold",80);
    
    ArrayList<User> userList = new ArrayList<User>();
    
    ArrayList<String> command = new ArrayList<>();
    String inventory = "Inventory";
    String buy = "Buy";
    String sell = "Sell";
    String userOne = "UserOne";
    String userTwo = "UserTwo";
    String userThree = "UserThree";
    String marketPlace = "Marketplace";
    CountDownLatch count = new CountDownLatch( 3 );
    
    
    public SocketHandlerTest() throws IOException
    {
        socket = new Socket("127.0.0.1", 11000);
        userManager = new UserManager(userList);
        marketplace = new Marketplace(marketResources);
        socketHandler = new SocketHandler(socket, userManager, marketplace);
        printWriter = new PrintWriter( socket.getOutputStream(), true );
    }
    
    @Before
    public void setup() throws IOException {
        
        resourceList.clear();
        resourceList.add(wood);
        resourceList.add(iron);
        resourceList.add(steel);
        resourceList.add(silver);
        resourceList.add(gold);
        
        marketResources.clear();
        marketResources.add(wood1);
        marketResources.add(iron1);
        marketResources.add(steel1);
        marketResources.add(silver1);
        marketResources.add(gold1);
        
        User user1 = new User(userOne, "password", resourceList, 5000);
        User user2 = new User(userTwo, "password", resourceList, 100);
        User user3 = new User(userThree, "password", resourceList, 10000);
        
        userList.clear();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        
    }
    
    @After
    public void deconstruct() throws IOException
    {
        server.serverSocket.close();
        serverThread.interrupt();
    }
    
    // Test to ensure runTest returns -1 when an invalid command is given
    @Test
    public void givenInvalidCommandThenMenuReturnsFalse() throws IOException {
        assertEquals(-1, socketHandler.runTest(userOne, "password", "Invenztttory-Marketplace"));
        
    }
    
    // test that the runTest method returns 0 when a valid command is given
    // and depending on command displays the correct output for the user
    // In this case to display the user's inventory
    @Test
    public void givenValidCommandThenGetUserInventory() throws IOException {
        assertEquals(0, socketHandler.runTest(userOne, "password", "Inventory-UserOne"));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        
    }
    
    // test that the runTest method returns 0 when a valid command is given
    // and depending on command displays the correct output for the user
    // In this case to display the marketplace inventory
    @Test
    public void givenValidCommandThenGetMarketInventory() throws IOException {
        assertEquals(0, socketHandler.runTest(userOne, "password", "Inventory-Marketplace"));
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenValidCommandThenReturnOtherOnlineUsers() throws IOException {
        assertEquals(0, socketHandler.runTest(userOne, "password", "Users-UserOne"));
        
    }
    
    // Testing that the runTest method correctly calls the getUsers method which will display
    // all other users that are currently using the system except from the user that called the command
    @Test
    public void givenMultipleOnlineUsersThenReturnOtherOnlineUsers() throws IOException {
        socketHandler.runTest(userTwo, "password", "Users-UserOne");
        socketHandler.runTest(userThree, "password", "Users-UserOne");
        assertEquals(2, socketHandler.runTest(userOne, "password", "Users-UserOne"));
        
    }
    
    // Test to ensure that runTest returns 0 when a valid command is given
    // and depending on command displays the correct output for the user
    // In this case userOne is transferring 1000 currency to user 2
    // and the assert checks that the funds have been transferred correctly
    // by checking the funds of both users
    @Test
    public void givenValidCommandThenParseCommandTransfersCurrency() {
        String command = "Transfer-UserOne-UserTwo-1000";
        
        assertEquals(0, socketHandler.runTest(userOne, "password", command));
        assertEquals(4000, userManager.getUser(userOne).getFunds());
        assertEquals(1100, userManager.getUser(userTwo).getFunds());
        
    }
    
    // Test to ensure that runTest returns -1 when an invalid command is given
    // In this case the command is invalid as the source user does not exist
    // and the assert checks that the funds of both users have not been changed
    @Test
    public void givenInvalidSourceThenParseCommandReturnsFalse() {
        String command = "Transfer-UserNine-UserTwo-1000";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        assertEquals(100, userManager.getUser( userTwo ).getFunds());
        
    }
    
    // Test to ensure that runTest returns -1 when an invalid command is given
    // In this case the command is invalid as the destination user does not exist
    // and the assert checks that the funds of both users have not been changed
    @Test
    public void givenInvalidDestinationThenParseCommandReturnsFalse() {
        String command = "Transfer-UserOne-UserNine-1000";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        assertEquals(100, userManager.getUser(userTwo).getFunds());
        
    }
    
    // Test to ensure that runTest returns -1 when an invalid command is given
    // In this case the command is invalid as the source and destination users do not exist
    // and the assert checks that the funds of both users have not been changed
    @Test
    public void givenInvalidSourceAndDestinationThenParseCommandReturnsFalse() {
        String command = "Transfer-UserNine-UserNinety-1000";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        assertEquals(5000, userManager.getUser( userOne ).getFunds());
        assertEquals(100, userManager.getUser( userTwo ).getFunds());
        
    }
    
    // Test to ensure that runTest returns -1 when an invalid command is given
    // In this case the command is invalid as the source user does not have sufficient funds
    // and the assert checks that the funds of both users have not been changed
    @Test
    public void givenInsufficientFundsThenParseCommandReturnsFalse() {
        String command = "Transfer-UserOne-UserTwo-10000";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        assertEquals(100, userManager.getUser( userTwo ).getFunds());
        
    }
    
    
    // Test that runTest returns 0 when a valid command is given
    // In this instance UserOne is buying 10 iron from the marketplace
    // and the assert checks that the funds and resources of both the user and marketplace
    // have been updated correctly
    @Test
    public void givenValidCommandThenBuyItems() {
        String command = "Buy-Marketplace-UserOne-2-10";
        
        assertEquals(0, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(110, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(4980, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(990, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the resourceID is invalid
    // and the assert checks that the funds and resources of both the user and marketplace
    // remain the same as before the method call
    @Test
    public void givenInValidResourceIdThenBuyItemsReturnsError() {
        String command = "Buy-Marketplace-UserOne-8-10";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the username is invalid
    // and the assert checks that the funds and resources of both the user and marketplace
    // remain the same as before the method call
    @Test
    public void givenInValidUsernameThenBuyItemsReturnsError() {
        String command = "Buy-Marketplace-UserNnine-1-10";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the marketplace does not have sufficient quantity of the resource
    // and the assert checks that the funds and resources of both the user and marketplace
    // remain the same as before the method call
    @Test
    public void givenInsufficientResourceQuantityThenBuyItemsReturnsError() {
        serverThread.start();
        String command = "Buy-Marketplace-UserOne-2-1100";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
        
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the user does not have sufficient funds
    // and the assert checks that the funds and resources of both the user and marketplace
    // remain the same as before the method call
    @Test
    public void givenInsufficientUserFundsThenBuyItemsReturnsError() {
        
        String command = "Buy-Marketplace-UserTwo-1-200";
        
        assertEquals(-1, socketHandler.runTest("UserTwo", "password", command));
        
        assertEquals(1000, userManager.getUser(userTwo).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userTwo).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userTwo).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userTwo).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userTwo).getResourceQuantity( 5 ));
        assertEquals(100, userManager.getUser(userTwo).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
        
        
    }
    
    // Test that runTest returns 0 when a valid command is given
    // In this instance UserOne is selling 10 iron to the marketplace
    // and the assert checks that the funds and resources of both the user and marketplace
    // have been updated correctly
    @Test
    public void givenValidCommandThenSellItems() {
        String command = "Sell-Marketplace-UserOne-2-10";
        
        assertEquals(0, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(90, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5010, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1010, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the resourceID is invalid
    // and the assert checks that the funds and resources of both the user and marketplace
    // remain the same as before the method call
    @Test
    public void givenInValidResourceIdThenSellItemsReturnsError() {
        String command = "Sell-Marketplace-UserOne-8-10";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the username is invalid
    // and the assert checks that the funds and resources of both the user and marketplace
    // remain the same as before the method call
    @Test
    public void givenInValidUsernameThenSellItemsReturnsError() {
        String command = "Sell-Marketplace-UserNnine-1-10";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the user does not have sufficient resources
    // and the assert checks that the funds and resources of both the user and marketplace
    
    @Test
    public void givenInsufficientResourceQuantityThenSellItemsReturnsError() {
        String command = "Sell-Marketplace-UserOne-2-200";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns 0 when a valid command is given
    // In this instance addFunds is called to add 200 currency to userOne
    // and the assert checks that the funds of userOne have been updated correctly
    @Test
    public void givenValidCommandThenAddFunds() {
        String command = "AddFunds-UserOne-200";
        
        assertEquals(0, socketHandler.runTest(userOne, "password", command));
        assertEquals(5200, userManager.getUser(userOne).getFunds());
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the username is invalid
    // and the assert checks that the funds of userOne have not been changed
    @Test
    public void givenInvalidUsernameThenAddFundsReturnsError() {
        String command = "AddFunds-UserNine-200";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
    }
    
    // Test that runTest returns 0 when a valid command is given
    // In this instance removeFunds is called to remove 200 currency from userOne
    // and the assert checks that the funds of userOne have been updated correctly
    @Test
    public void givenValidCommandThenRemoveFunds() {
        String command = "RemoveFunds-UserOne-200";
        
        assertEquals(0, socketHandler.runTest(userOne, "password", command));
        assertEquals(4800, userManager.getUser(userOne).getFunds());
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the username is invalid
    // and the assert checks that the funds of userOne have not been changed
    @Test
    public void givenInvalidUsernameThenRemoveFundsReturnsError() {
        String command = "RemoveFunds-UserNine-200";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the user does not have sufficient funds to remove
    // and the assert checks that the funds of userOne have not been changed
    @Test
    public void givenInsufficientFundsThenRemoveFundsReturnsError() {
        String command = "RemoveFunds-UserOne-10000";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
    }
    
    // Test that runTest returns 0 when a valid command is given
    // In this instance addResource is called to add 100 iron to userOne
    // and the assert checks that the resources of userOne have been updated correctly
    @Test
    public void givenValidCommandThenAddResourceToUser() {
        String command = "AddResource-UserOne-2-100";
        
        assertEquals(0, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(200, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the username is invalid
    // and the assert checks that the resources of userOne have not been changed
    @Test
    public void givenInvalidUsernameThenAddResourceToUserReturnsError() {
        String command = "AddResource-UserNine-2-100";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the resourceID is invalid
    // and the assert checks that the resources of userOne have not been changed
    @Test
    public void givenInvalidResourceIdThenAddResourceToUserReturnsError() {
        String command = "AddResource-UserOne-6-100";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
    }

    // Test that runTest returns 0 when a valid command is given
    // In this instance removeResource is called to remove 100 iron from userOne
    // and the assert checks that the resources of userOne have been updated correctly
    @Test
    public void givenValidCommandThenRemoveResourceFromUser() {
        String command = "RemoveResource-UserOne-2-100";
        
        assertEquals(0, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(0, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the username is invalid
    // and the assert checks that the resources of userOne have not been changed
    @Test
    public void givenInvalidUsernameThenRemoveResourceFromUserReturnsError() {
        String command = "RemoveResource-UserNine-2-100";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the resourceID is invalid
    // and the assert checks that the resources of userOne have not been changed
    @Test
    public void givenInvalidResourceIdThenRemoveResourceFromUserReturnsError() {
        String command = "RemoveResource-UserOne-9-100";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    // Test that runTest returns -1 when an invalid command is given
    // In this instance the command is invalid as the user does not have sufficient resources to remove
    // and the assert checks that the resources of userOne have not been changed
    @Test
    public void givenInsufficientResourceQuantityThenRemoveResourceFromUserReturnsError() {
        String command = "RemoveResource-UserNine-5-100";
        
        assertEquals(-1, socketHandler.runTest(userOne, "password", command));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        assertEquals(5000, userManager.getUser(userOne).getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
        
    }
    
    
    
    
    
    
    
    
    
}