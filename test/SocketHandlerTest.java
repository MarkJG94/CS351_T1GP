import org.junit.Before;
import org.junit.Test;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocketHandlerTest {
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
    User user1 = new User("User One", "password", resourceList, 5000);
    User user2 = new User("User Two", "password", resourceList, 100);
    User user3 = new User("User Three", "password", resourceList, 10000);
    User user4 = new User("User Four", "password", resourceList, 1);

    ArrayList<String> command = new ArrayList<>();
    String inventory = "Inventory";
    String buy = "Buy";
    String sell = "Sell";
    String userOne = "User One";
    String marketPlace = "Marketplace";

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

    }

    @Test
    public void givenValidCommandThenGetUserInventory() throws IOException {
        assertEquals(0, socketHandler.runTest("User One", "password", "Inventory-User One"));

        assertEquals(1000, userManager.getUser("User One").userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser("User One").userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser("User One").userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser("User One").userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser("User One").userResources.get(4).getQuantity());

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




//
//    @Test
//    public void givenValidCommandThenParseCommandTransfersCurrency() {
//        String command = "Transfer-User One-User Two-1000";
//
//        assertEquals(true, socketHandler.parseCommand("User One", printWriter, command));
//        assertEquals(4000, userList.get(0).getFunds());
//        assertEquals(1100, userList.get(1).getFunds());
//
//    }
//
//    @Test
//    public void givenInvalidDestinationThenParseCommandReturnsFalse() {
//        String command = "Transfer-User One-User Nine-1000";
//
//        assertEquals(false, socketHandler.parseCommand("User One", printWriter, command));
//        assertEquals(5000, userList.get(0).getFunds());
//
//    }
//
//    @Test
//    public void givenInsufficientFundsThenParseCommandReturnsFalse() {
//        String command = "Transfer-User One-User Two-100000";
//
//        assertEquals(false, socketHandler.parseCommand("User One", printWriter, command));
//        assertEquals(5000, userList.get(0).getFunds());
//        assertEquals(100, userList.get(1).getFunds());
//
//    }
//
//    @Test
//    public void givenValidCommandThenDisplayOnlineUsers() {
//        String command = "Users";
//
//        assertEquals(true, socketHandler.parseCommand("User One", printWriter, command));
//
//
//    }
}