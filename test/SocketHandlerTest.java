import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocketHandlerTest {
    Server server = new Server(1);
    SocketHandler socketHandler;
    Socket socket = new Socket();
    UserManager userManager;
    Marketplace marketplace;

    PrintWriter printWriter;

    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    Resource wood = new Resource(1, 1, 1000, "wood", 1);
    Resource iron = new Resource(2, 2, 100, "iron", 1);
    Resource steel = new Resource(3, 5, 10, "silver",4);
    Resource silver = new Resource(4, 10, 5, "gold",8);
    Resource gold = new Resource(5, 100, 1, "gold",80);

    ArrayList<Resource> marketResources = new ArrayList<Resource>();
    Resource wood1 = new Resource(1, 1, 10000, "wood", 1);
    Resource iron1 = new Resource(2, 2, 1000, "iron", 1);
    Resource steel1 = new Resource(3, 5, 100, "silver",4);
    Resource silver1 = new Resource(4, 10, 10, "gold",8);
    Resource gold1 = new Resource(5, 100, 1, "gold",80);

    ArrayList<User> userList = new ArrayList<User>();
    User user1;
    User user2;
    User user3;
    User user4;

    ArrayList<String> command = new ArrayList<>();
    String inventory = "Inventory";
    String buy = "Buy";
    String sell = "Sell";
    String userOne = "User One";
    String marketPlace = "Marketplace";

    public SocketHandlerTest() throws IOException {
    }

    @Before
    public void setup() throws IOException {
        resourceList.add(wood);
        resourceList.add(iron);
        resourceList.add(steel);
        resourceList.add(silver);
        resourceList.add(gold);

        marketResources.add(wood1);
        marketResources.add(iron1);
        marketResources.add(steel1);
        marketResources.add(silver1);
        marketResources.add(gold1);

        user1 = new User("User One", "password", resourceList, 5000);
        user2 = new User("User Two", "password", resourceList, 100);
        user3 = new User("User Three", "password", resourceList, 10000);
        user4 = new User("User Four", "password", resourceList, 1);

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);


        socket = new Socket("127.0.0.1", 11000);
        userManager = new UserManager(userList);
        marketplace = new Marketplace(marketResources);
        socketHandler = new SocketHandler(socket, userManager, marketplace);
        printWriter = new PrintWriter( socket.getOutputStream(), true );


    }

    @Test
    public void givenInvalidCommandThenMenuReturnsFalse() throws IOException {
        assertEquals(-1, socketHandler.runTest("User One", "password", "Invenztttory-Marketplace"));
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenGetUserInventory() throws IOException {
        assertEquals(0, socketHandler.runTest("User One", "password", "Inventory-User One"));

        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenGetMarketInventory() throws IOException {
        assertEquals(0, socketHandler.runTest("User One", "password", "Inventory-Marketplace"));


        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenReturnOtherOnlineUsers() throws IOException {
        assertEquals(0, socketHandler.runTest("User One", "password", "Users-User One"));
        server.serverSocket.close();

    }

    @Test
    public void givenMultipleOnlineUsersThenReturnOtherOnlineUsers() throws IOException {
        socketHandler.runTest("User Two", "password", "Users-User One");
        socketHandler.runTest("User Three", "password", "Users-User One");
        assertEquals(2, socketHandler.runTest("User One", "password", "Users-User One"));
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenParseCommandTransfersCurrency() throws IOException {
        String command = "Transfer-User One-User Two-1000";

        assertEquals(0, socketHandler.runTest("User One", "password", command));
        assertEquals(4000, userList.get(0).getFunds());
        assertEquals(1100, userList.get(1).getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenInvalidSourceThenParseCommandReturnsFalse() throws IOException {
        String command = "Transfer-User Nine-User Two-1000";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenInvalidDestinationThenParseCommandReturnsFalse() throws IOException {
        String command = "Transfer-User One-User Nine-1000";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenInvalidSourceAndDestinationThenParseCommandReturnsFalse() throws IOException {
        String command = "Transfer-User Nine-User Ninety-1000";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenInsufficientFundsThenParseCommandReturnsFalse() throws IOException {
        String command = "Transfer-User One-User Two-10000";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userList.get(0).getFunds());
        assertEquals(100, userList.get(1).getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenBuyItems() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInValidResourceIdThenBuyItemsReturnsError() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInValidUsernameThenBuyItemsReturnsError() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInsufficientResourceQuantityThenBuyItemsReturnsError() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInsufficientUserFundsThenBuyItemsReturnsError() throws IOException {
        String command = "Buy-Marketplace-User Two-1-200";

        assertEquals(-1, socketHandler.runTest("User Two", "password", command));

        assertEquals(1000, userManager.getUser("User Two").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User Two").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User Two").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User Two").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User Two").userResources.get(4).getQuantity());
        assertEquals(100, userManager.getUser("User Two").getFunds());


        assertEquals(10000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals(1000, marketplace.getResourceDetails(2).getQuantity());
        assertEquals(100, marketplace.getResourceDetails(3).getQuantity());
        assertEquals(10, marketplace.getResourceDetails(4).getQuantity());
        assertEquals(1, marketplace.getResourceDetails(5).getQuantity());
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenSellItems() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInValidResourceIdThenSellItemsReturnsError() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInValidUsernameThenSellItemsReturnsError() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInsufficientResourceQuantityThenSellItemsReturnsError() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenAddFunds() throws IOException {
        String command = "AddFunds-User One-200";

        assertEquals(0, socketHandler.runTest("User One", "password", command));
        assertEquals(5200, userManager.getUser("User One").getFunds());
        server.serverSocket.close();

    }

    @Test
    public void givenInvalidUsernameThenAddFundsReturnsError() throws IOException {
        String command = "AddFunds-User Nine-200";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenRemoveFunds() throws IOException {
        String command = "RemoveFunds-User One-200";

        assertEquals(0, socketHandler.runTest("User One", "password", command));
        assertEquals(4800, userManager.getUser("User One").getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenInvalidUsernameThenRemoveFundsReturnsError() throws IOException {
        String command = "RemoveFunds-User Nine-200";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenInsufficientFundsThenRemoveFundsReturnsError() throws IOException {
        String command = "RemoveFunds-User One-10000";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenAddResourceToUser() throws IOException {
        String command = "AddResource-User One-2-100";

        assertEquals(0, socketHandler.runTest("User One", "password", command));

        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(200, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();

    }

    @Test
    public void givenInvalidUsernameThenAddResourceToUserReturnsError() throws IOException {
        String command = "AddResource-User Nine-2-100";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));

        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();

    }

    @Test
    public void givenInvalidResourceIdThenAddResourceToUserReturnsError() throws IOException {
        String command = "AddResource-User One-6-100";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));

        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenValidCommandThenRemoveResourceFromUser() throws IOException {
        String command = "RemoveResource-User One-2-100";

        assertEquals(0, socketHandler.runTest("User One", "password", command));

        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(0, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();
    }

    @Test
    public void givenInvalidUsernameThenRemoveResourceFromUserReturnsError() throws IOException {
        String command = "RemoveResource-User Nine-2-100";

        assertEquals(-1, socketHandler.runTest("User One", "password", command));

        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());
        assertEquals(5000, userManager.getUser("User One").getFunds());
        server.serverSocket.close();

    }

    @Test
    public void givenInvalidResourceIdThenRemoveResourceFromUserReturnsError() throws IOException {
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
        server.serverSocket.close();
    }

    @Test
    public void givenInsufficientResourceQuantityThenRemoveResourceFromUserReturnsError() throws IOException {
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
        server.serverSocket.close();
    }
}