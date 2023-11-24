import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

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


    String userOne = "UserOne";
    String userTwo = "UserTwo";
    String userThree = "UserThree";


    public UserTest() throws IOException
    {
        socket = new Socket("127.0.0.1", 11000);
        userManager = new UserManager(userList);
        marketplace = new Marketplace(marketResources);
        socketHandler = new SocketHandler(socket, userManager, marketplace);
        printWriter = new PrintWriter( socket.getOutputStream(), true );
    }

    @Before
    public void setUp() throws Exception {
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

    @Test
    public void givenValidUserNameAndAmountThenAddFunds() throws IOException {

        assertEquals(5100, userManager.getUser(userOne).addFunds(100));
        assertEquals(200, userManager.getUser(userTwo).addFunds(100));
        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenValidUserNameAndInvalidAmountThenAddFundsReturnsFalse() throws IOException {

        assertEquals(-1, userManager.getUser(userOne).addFunds(-100));
        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenValidUserNameAndAmountThenDeductFunds() throws IOException {

        assertEquals(4900, userManager.getUser(userOne).deductFunds(100));
        assertEquals(0, userManager.getUser(userTwo).deductFunds(100));
        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenValidUserNameAndInvalidAmountThenDeductFundsReturnsFalse() throws IOException {

        assertEquals(-1, userManager.getUser(userOne).deductFunds(-100));
        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenInvalidUserNameAndAmountThenDeductFundsReturnsFalse() throws IOException {

        assertEquals(-1, userManager.getUser(userOne).deductFunds(-100));
        server.serverSocket.close();
        serverThread.interrupt();
    }


    @Test
    public void givenValidInformationThenAddResource() throws IOException {

        userManager.getUser(userOne).addResource(1, 100);
        assertEquals(1100, userManager.getUser(userOne).userResources.get(0).getQuantity());

        userManager.getUser(userTwo).addResource(1, 200);
        assertEquals(1200, userManager.getUser(userTwo).userResources.get(0).getQuantity());

        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenInvalidResourceIdThenAddResourceReturnsFalse() throws IOException {

        assertEquals(false, userManager.getUser(userOne).addResource(11, 100));
        assertEquals(1000, userManager.getUser(userOne).userResources.get(0).getQuantity());
        assertEquals(1000, userManager.getUser(userTwo).userResources.get(0).getQuantity());
        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenInvalidQuantityThenAddResourceReturnsFalse() throws IOException {

        assertEquals(false, userManager.getUser(userOne).addResource(1, -100));
        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenValidInformationThenRemoveResource() throws IOException {

        userManager.getUser(userOne).addResource(1, 100);
        assertEquals(1100, userManager.getUser(userOne).userResources.get(0).getQuantity());

        userManager.getUser(userTwo).addResource(2, 100);
        assertEquals(200, userManager.getUser(userTwo).userResources.get(1).getQuantity());

        server.serverSocket.close();
        serverThread.interrupt();
    }


    @Test
    public void givenInvalidResourceIdThenRemoveResourceReturnsFalse() throws IOException {

        assertEquals(false, userManager.getUser(userOne).removeResource(11, 100));
        server.serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void givenInvalidQuantityThenRemoveResourceReturnsFalse() throws IOException {

        assertEquals(false, userManager.getUser(userOne).removeResource(1, -100));
        assertEquals(1000, userManager.getUser(userOne).userResources.get(0).getQuantity());
        server.serverSocket.close();
        serverThread.interrupt();
    }


    @Test
    public void givenCorrectAmountThenValidateCurrency() throws IOException {

        assertEquals(true, userManager.getUser(userOne).validateCurrency(5000));
        assertEquals(true, userManager.getUser(userTwo).validateCurrency(100));
        server.serverSocket.close();
        serverThread.interrupt();

    }

    @Test
    public void givenInvalidAmountThenValidateCurrencyReturnFalse() throws IOException {

        assertEquals(false, userManager.getUser(userOne).validateCurrency(50000));
        assertEquals(true, userManager.getUser(userOne).validateCurrency(5000));
        server.serverSocket.close();
        serverThread.interrupt();

    }

    @Test
    public void getFunds() throws IOException {

        assertEquals(5000, userManager.getUser(userOne).getFunds());
        assertEquals(100, userManager.getUser(userTwo).getFunds());
        server.serverSocket.close();
        serverThread.interrupt();

    }

    @Test
    public void getUserName() throws IOException {

        assertEquals("UserOne", userManager.getUser(userOne).getUsername());
        assertEquals("UserTwo", userManager.getUser(userTwo).getUsername());
        server.serverSocket.close();
        serverThread.interrupt();

    }

    @Test
    public void getResourceQuantity() throws IOException {

        assertEquals(1000, userManager.getUser(userOne).getResourceQuantity(1));
        assertEquals(100, userManager.getUser(userOne).getResourceQuantity(2));
        assertEquals(10, userManager.getUser(userOne).getResourceQuantity(3));

        assertEquals(1000, userManager.getUser(userTwo).getResourceQuantity(1));
        assertEquals(100, userManager.getUser(userTwo).getResourceQuantity(2));
        assertEquals(10, userManager.getUser(userTwo).getResourceQuantity(3));

        server.serverSocket.close();
        serverThread.interrupt();

    }

    @Test
    public void getUserInventory() throws IOException {

        assertEquals("wood", userManager.getUser(userOne).userResources.get(0).getName());
        assertEquals("iron", userManager.getUser(userOne).userResources.get(1).getName());
        assertEquals("silver", userManager.getUser(userOne).userResources.get(2).getName());
        assertEquals("gold", userManager.getUser(userOne).userResources.get(3).getName());

        assertEquals(1000, userManager.getUser(userOne).userResources.get(0).getQuantity());
        assertEquals(100, userManager.getUser(userOne).userResources.get(1).getQuantity());
        assertEquals(10, userManager.getUser(userOne).userResources.get(2).getQuantity());
        assertEquals(5, userManager.getUser(userOne).userResources.get(3).getQuantity());
        assertEquals(1, userManager.getUser(userOne).userResources.get(4).getQuantity());

        assertEquals(1, userManager.getUser(userOne).userResources.get(0).getCost());
        assertEquals(2, userManager.getUser(userOne).userResources.get(1).getCost());
        assertEquals(5, userManager.getUser(userOne).userResources.get(2).getCost());
        assertEquals(10, userManager.getUser(userOne).userResources.get(3).getCost());
        assertEquals(100, userManager.getUser(userOne).userResources.get(4).getCost());

        assertEquals(1, userManager.getUser(userOne).userResources.get(0).getValue());
        assertEquals(1, userManager.getUser(userOne).userResources.get(1).getValue());
        assertEquals(4, userManager.getUser(userOne).userResources.get(2).getValue());
        assertEquals(8, userManager.getUser(userOne).userResources.get(3).getValue());
        assertEquals(80, userManager.getUser(userOne).userResources.get(4).getValue());
        server.serverSocket.close();
        serverThread.interrupt();

    }

}