import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocketHandlerTest
{
    Server server = new Server(1);
    Thread serverThread = new Thread( server);
    SocketHandler socketHandler;
    Socket socket = new Socket();
    UserManager userManager;
    Marketplace marketplace;
    
    PrintWriter printWriter;
    
    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    Resource wood = new Resource(1, 1, 1000, "wood", 1);
    Resource iron = new Resource(2, 2, 100, "iron", 1);
    Resource stone = new Resource(3, 5, 10, "stone",4);
    Resource silver = new Resource(4, 10, 5, "silver",8);
    Resource gold = new Resource(5, 100, 1, "gold",80);
    
    ArrayList<Resource> marketResources = new ArrayList<Resource>();
    Resource wood1 = new Resource(1, 1, 10000, "wood", 1);
    Resource iron1 = new Resource(2, 2, 1000, "iron", 1);
    Resource stone1 = new Resource(3, 5, 100, "stone",4);
    Resource silver1 = new Resource(4, 10, 10, "silver",8);
    Resource gold1 = new Resource(5, 100, 1, "gold",80);
    
    ArrayList<User> userList = new ArrayList<User>();
    String userOne = "UserOne";
    String userTwo = "UserTwo";
    String userThree = "UserThree";
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
        resourceList.add(stone);
        resourceList.add(silver);
        resourceList.add(gold);
        
        marketResources.clear();
        marketResources.add(wood1);
        marketResources.add(iron1);
        marketResources.add(stone1);
        marketResources.add(silver1);
        marketResources.add(gold1);
        
        User user1 = new User("UserOne", "password", resourceList, 5000);
        User user2 = new User("UserTwo", "password", resourceList, 100);
        User user3 = new User("UserThree", "password", resourceList, 10000);
        
        userList.clear();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        
    }
    
    @Test
    public void givenInvalidCommandThenMenuReturnsFalse() throws IOException {
        assertEquals(-1, socketHandler.runTest("User One", "password", "Invenztttory-Marketplace"));
        
    }
    
    @Test
    public void givenValidCommandThenGetUserInventory() throws IOException {
        assertEquals(0, socketHandler.runTest("UserOne", "password", "Inventory-UserOne"));
        
        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity( 1 ));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity( 2 ));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity( 3 ));
        assertEquals(5, userManager.getUser(userOne).getResourceQuantity( 4 ));
        assertEquals(1, userManager.getUser(userOne).getResourceQuantity( 5 ));
        
    }
    
    @Test
    public void givenValidCommandThenGetMarketInventory() throws IOException {
        assertEquals(0, socketHandler.runTest("User One", "password", "Inventory-Marketplace"));
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenValidCommandThenReturnOtherOnlineUsers() throws IOException {
        assertEquals(0, socketHandler.runTest("User One", "password", "Users-User One"));
        
    }
    
    @Test
    public void givenMultipleOnlineUsersThenReturnOtherOnlineUsers() throws IOException {
        socketHandler.runTest("User Two", "password", "Users-User One");
        socketHandler.runTest("User Three", "password", "Users-User One");
        assertEquals(2, socketHandler.runTest("User One", "password", "Users-User One"));
        
    }
    
    @Test
    public void givenValidCommandThenParseCommandTransfersCurrency() {
        String command = "Transfer-User One-User Two-1000";
        
        assertEquals(0, socketHandler.runTest("User One", "password", command));
        assertEquals(4000, userList.get(0).getFunds());
        assertEquals(1100, userList.get(1).getFunds());
        
    }
    
    @Test
    public void givenInvalidSourceThenParseCommandReturnsFalse() {
        String command = "Transfer-User Nine-User Two-1000";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        
    }
    
    @Test
    public void givenInvalidDestinationThenParseCommandReturnsFalse() {
        String command = "Transfer-User One-User Nine-1000";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        
    }
    
    @Test
    public void givenInvalidSourceAndDestinationThenParseCommandReturnsFalse() {
        String command = "Transfer-User Nine-User Ninety-1000";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        
    }
    
    @Test
    public void givenInsufficientFundsThenParseCommandReturnsFalse() {
        String command = "Transfer-User One-User Two-10000";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        
    }
    
    @Test
    public void givenValidCommandThenBuyItems() {
        String command = "Buy-Marketplace-User One-2-10";
        
        assertEquals(0, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(110, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(4980, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(990, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInValidResourceIdThenBuyItemsReturnsError() {
        String command = "Buy-Marketplace-User One-8-10";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInValidUsernameThenBuyItemsReturnsError() {
        String command = "Buy-Marketplace-User Nnine-1-10";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInsufficientResourceQuantityThenBuyItemsReturnsError() {
        String command = "Buy-Marketplace-User One-2-1100";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInsufficientUserFundsThenBuyItemsReturnsError() {
        String command = "Buy-Marketplace-UserTwo-1-200";
        
        assertEquals(-1, socketHandler.runTest("UserTwo", "password", command));
        
        assertEquals(1000, userManager.getUser(userTwo).userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("UserTwo").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User Two").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User Two").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User Two").userResources.get(4).getQuantity());
        assertEquals(100, userManager.getUser("User Two").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenValidCommandThenSellItems() {
        String command = "Sell-Marketplace-User One-2-10";
        
        assertEquals(0, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(90, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5010, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1010, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInValidResourceIdThenSellItemsReturnsError() {
        String command = "Sell-Marketplace-User One-8-10";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInValidUsernameThenSellItemsReturnsError() {
        String command = "Sell-Marketplace-User Nnine-1-10";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInsufficientResourceQuantityThenSellItemsReturnsError() {
        String command = "Sell-Marketplace-User One-2-200";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenValidCommandThenAddFunds() {
        String command = "AddFunds-User One-200";
        
        assertEquals(0, socketHandler.runTest("User One", "password", command));
        assertEquals(5200, userManager.getUser("User One").getFunds());
        
    }
    
    @Test
    public void givenInvalidUsernameThenAddFundsReturnsError() {
        String command = "AddFunds-User Nine-200";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
    }
    
    @Test
    public void givenValidCommandThenRemoveFunds() {
        String command = "RemoveFunds-User One-200";
        
        assertEquals(0, socketHandler.runTest("User One", "password", command));
        assertEquals(4800, userManager.getUser("User One").getFunds());
        
    }
    
    @Test
    public void givenInvalidUsernameThenRemoveFundsReturnsError() {
        String command = "RemoveFunds-User Nine-200";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
    }
    
    @Test
    public void givenInsufficientFundsThenRemoveFundsReturnsError() {
        String command = "RemoveFunds-User One-10000";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
    }
    
    @Test
    public void givenValidCommandThenAddResourceToUser() {
        String command = "AddResource-User One-2-100";
        
        assertEquals(0, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(200, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
    }
    
    @Test
    public void givenInvalidUsernameThenAddResourceToUserReturnsError() {
        String command = "AddResource-User Nine-2-100";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
    }
    
    @Test
    public void givenInvalidResourceIdThenAddResourceToUserReturnsError() {
        String command = "AddResource-User One-6-100";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
    }
    
    @Test
    public void givenValidCommandThenRemoveResourceFromUser() {
        String command = "RemoveResource-User One-2-100";
        
        assertEquals(0, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(0, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
    }
    
    @Test
    public void givenInvalidUsernameThenRemoveResourceFromUserReturnsError() {
        String command = "RemoveResource-User Nine-2-100";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        
    }
    
    @Test
    public void givenInvalidResourceIdThenRemoveResourceFromUserReturnsError() {
        String command = "RemoveResource-User One-9-100";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
    }
    
    @Test
    public void givenInsufficientResourceQuantityThenRemoveResourceFromUserReturnsError() {
        String command = "RemoveResource-User Nine-5-100";
        
        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        
        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        
        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
        
    }
    
    
    
    
    
    
    
    
    
}