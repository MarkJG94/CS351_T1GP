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


import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrencyTest
{
    Server server = new Server();
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
    User user1 = new User("UserOne", "password", resourceList, 5000);
    User user2 = new User("UserTwo", "password", resourceList, 100);
    User user3 = new User("UserThree", "password", resourceList, 10000);
    User user4 = new User("UserFour", "password", resourceList, 1);
    
    User user5 = new User("UserFive", "password", resourceList, 0);
    
    ArrayList<String> command = new ArrayList<>();
    String inventory = "Inventory";
    String buy = "Buy";
    String sell = "Sell";
    String userOne = "UserOne";
    String userTwo = "UserTwo";
    String userThree = "UserThree";
    String marketPlace = "Marketplace";
    CountDownLatch count = new CountDownLatch( 3 );
    
    
    public ConcurrencyTest() throws IOException
    {
    }
    
    @Before
    public void setup() throws IOException {
        //serverThread.start();
        
        
//        try {
//            client1.runClient();
//        } catch ( IOException | NotBoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            client2.runClient();
//        } catch ( IOException | NotBoundException e) {
//            e.printStackTrace();
//        }
        
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
        userList.add(user5);
        
        socket = new Socket("127.0.0.1", 11000);
        userManager = new UserManager(userList);
        marketplace = new Marketplace(marketResources);
        socketHandler = new SocketHandler(socket, userManager, marketplace);
        printWriter = new PrintWriter( socket.getOutputStream(), true );
    }
    
    // Test that the addFunds method in UserManager is thread safe
    @Test
    public void testAddFundsConcurrency() throws InterruptedException, IOException
    {
        serverThread.start();
        Thread thread1 = new Thread( () ->
                                     {
                                         userManager.addFunds( userOne, 100 );
                                         count.countDown();
                                     } );
        thread1.start();
        
        Thread thread2 = new Thread( () ->
                                     {
                                         userManager.addFunds( userOne, 100 );
                                         count.countDown();
                                     } );
        thread2.start();
        
        Thread thread3 = new Thread( () ->
                                     {
                                         userManager.addFunds( userOne, 100 );
                                         count.countDown();
                                     } );
        thread3.start();
        
        try
        {
            count.await();
            assertEquals( 5300, userManager.getUser( userOne ).getFunds() );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        // stop all threads
        server.serverSocket.close();
        serverThread.interrupt();
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }
    
    // Test that the deductFunds method in User is thread safe
    @Test
    public void testDeductFundsConcurrency() throws IOException, InterruptedException
    {
        serverThread.start();
        Thread thread1 = new Thread( () ->
                                     {
                                         userManager.deductFunds(userTwo , 25 );
                                         count.countDown();
                                     } );
        thread1.start();
        
        Thread thread2 = new Thread( () ->
                                     {
                                         userManager.deductFunds(userTwo , 25 );
                                         count.countDown();
                                     } );
        thread2.start();
        
        Thread thread3 = new Thread( () ->
                                     {
                                         userManager.deductFunds(userTwo , 25 );
                                         count.countDown();
                                     } );
        thread3.start();
        
        try
        {
            count.await();
            assertEquals( 25, userManager.getUser( userTwo ).getFunds() );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        // stop all threads
        server.serverSocket.close();
        serverThread.interrupt();
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }
    
    // Test that deductFunds method in user is thread safe when funds are insufficient to deduct
    @Test
    public void testDeductInsufficientFundsConcurrency() throws InterruptedException, IOException
    {
        serverThread.start();
        Thread thread1 = new Thread( () ->
                                     {
                                         userManager.deductFunds(userTwo , 50 );
                                         count.countDown();
                                     } );
        thread1.start();
        
        Thread thread2 = new Thread( () ->
                                     {
                                         userManager.deductFunds(userTwo , 50 );
                                         count.countDown();
                                     } );
        thread2.start();
        
        Thread thread3 = new Thread( () ->
                                     {
                                         userManager.deductFunds(userTwo , 50 );
                                         count.countDown();
                                     } );
        thread3.start();
        
        try
        {
            count.await();
            assertEquals( 0, userManager.getUser( userTwo ).getFunds() );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        // stop all threads
        server.serverSocket.close();
        serverThread.interrupt();
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }
    
    // Test that the transferFunds method in UserManager is thread safe
    @Test
    public void testTransferFundsConcurrency() throws InterruptedException, IOException
    {
        serverThread.start();
        Thread thread1 = new Thread( () ->
                                     {
                                         try
                                         {
                                             userManager.transferFunds( userOne, userTwo, 100 );
                                         }
                                         catch ( IOException e )
                                         {
                                             throw new RuntimeException( e );
                                         }
                                         count.countDown();
                                     } );
        thread1.start();
        
        Thread thread2 = new Thread( () ->
                                     {
                                         try
                                         {
                                             userManager.transferFunds( userTwo, userThree, 50 );
                                         }
                                         catch ( IOException e )
                                         {
                                             throw new RuntimeException( e );
                                         }
                                         count.countDown();
                                     } );
        thread2.start();
        
        Thread thread3 = new Thread( () ->
                                     {
                                         try
                                         {
                                             userManager.transferFunds( userThree, userOne, 200 );
                                         }
                                         catch ( IOException e )
                                         {
                                             throw new RuntimeException( e );
                                         }
                                         count.countDown();
                                     } );
        thread3.start();
        
        try
        {
            count.await();
            assertEquals( 5100, userManager.getUser( userOne ).getFunds() );
            assertEquals( 150, userManager.getUser( userTwo ).getFunds() );
            assertEquals( 9850, userManager.getUser( userThree ).getFunds() );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        // stop all threads
        server.serverSocket.close();
        serverThread.interrupt();
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }
    
    // Test that the transferFunds method in UserManager is thread safe when funds are insufficient from a user to
    // transfer
    @Test
    public void testTransferInsufficientFundsConcurrency() throws InterruptedException, IOException
    {
        serverThread.start();
        Thread thread1 = new Thread( () ->
                                     {
                                         try
                                         {
                                             userManager.transferFunds( userOne, userTwo, 100 );
                                         }
                                         catch ( IOException e )
                                         {
                                             throw new RuntimeException( e );
                                         }
                                         count.countDown();
                                     } );
        thread1.start();

        Thread thread2 = new Thread( () ->
                                     {
                                         try
                                         {
                                             userManager.transferFunds( userTwo, userThree, 250 );
                                         }
                                         catch ( IOException e )
                                         {
                                             throw new RuntimeException( e );
                                         }
                                         count.countDown();
                                     } );
        thread2.start();

        Thread thread3 = new Thread( () ->
                                     {
                                         try
                                         {
                                             userManager.transferFunds( userThree, userOne, 200 );
                                         }
                                         catch ( IOException e )
                                         {
                                             throw new RuntimeException( e );
                                         }
                                         count.countDown();
                                     } );
        thread3.start();

        try
        {
            count.await();
            assertEquals( 5100, userManager.getUser( userOne ).getFunds() );
            assertEquals( 200, userManager.getUser( userTwo ).getFunds() );
            assertEquals( 9800, userManager.getUser( userThree ).getFunds() );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }

        // stop all threads
        server.serverSocket.close();
        serverThread.interrupt();
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }
    
    // Test that multiple users can sell to the marketplace at the same time and that the marketplace is thread safe
    // Assume that all prerequisites are met for the function to execute the actual logic
    @Test
    public void testMultipleUsersSellResources() throws InterruptedException, IOException, IndexOutOfBoundsException
    {
        serverThread.start();
        Thread thread1 = new Thread( () ->
                                     {
                                         userManager.removeResource( 1, 10, userOne );
                                         marketplace.addResourceToMarket( 1, 10 );
                                         userManager.addFunds( userOne, 10 );
                                         count.countDown();
                                     } );
        thread1.start();
        
        Thread thread2 = new Thread( () ->
                                     {
                                         userManager.removeResource( 1, 15, userTwo );
                                         marketplace.addResourceToMarket( 1, 15 );
                                         userManager.addFunds( userTwo, 15 );
                                         count.countDown();
                                     } );
        thread2.start();

        Thread thread3 = new Thread( () ->
                                     {
                                         userManager.removeResource( 2, 10, userThree );
                                         marketplace.addResourceToMarket( 2, 10 );
                                         userManager.addFunds( userThree, 20 );
                                         count.countDown();
                                     } );
        thread3.start();
        
        try
        {
            count.await();
            //User One
            assertEquals( 5010, userManager.getUser( userOne ).getFunds() );
            assertEquals( 990, userManager.getUser( userOne ).getResourceQuantity( 1 ) );
            
            //User Two
            assertEquals( 115, userManager.getUser( userTwo ).getFunds() );
            assertEquals( 985, userManager.getUser( userTwo ).getResourceQuantity( 1 ) );
            
            //User Three
            assertEquals( 10020, userManager.getUser( userThree ).getFunds() );
            assertEquals( 90, userManager.getUser( userThree ).getResourceQuantity( 2 ) );
            
            //Marketplace assertions
            assertEquals( 10025, marketplace.getResourceQuantity( 1 ) );
            assertEquals( 1010, marketplace.getResourceQuantity( 2 ) );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        
        // stop all threads
        server.serverSocket.close();
        serverThread.interrupt();
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }
    
    
}

