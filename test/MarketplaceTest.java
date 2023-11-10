import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MarketplaceTest {

    Marketplace marketplace = new Marketplace();
    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    Resource wood = new Resource(1, 1, 1000, "wood");
    Resource iron = new Resource(2, 2, 1000, "iron");
    Resource silver = new Resource(3, 5, 1000, "silver");
    Resource gold = new Resource(4, 10, 1000, "gold");

    ArrayList<User> userList = new ArrayList<User>();
    User user1 = new User("User One", resourceList, 10000);
    User user2 = new User("User Two", resourceList, 100);
    User user3 = new User("User Three", resourceList, 10000);
    User user4 = new User("User Four", resourceList, 1);

    @BeforeEach
    void setup(){
        resourceList.add(wood);
        resourceList.add(iron);
        resourceList.add(silver);
        resourceList.add(gold);
        marketplace.marketResources = resourceList;

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        marketplace.userList = userList;

    }

    @Test
    void givenValidThenUserExists() {

        assertEquals(true, marketplace.userExists("User One"));
    }

    @Test
    void givenInvalidThenUserExistsReturnFalse() {

        assertEquals(false, marketplace.userExists("User Five"));
    }

    @Test
    void givenValidThenGetFunds() {

        assertEquals(10000, marketplace.getFunds("User One"));
    }

    @Test
    void givenInvalidThenGetFundsReturnsError() {

        assertEquals(-1, marketplace.getFunds("User Five"));
    }

    @Test
    void givenValidThenAddResource() {
        assertEquals(true, marketplace.addResource(1,100));
        assertEquals(1100, marketplace.marketResources.get(0).getQuantity());
    }

    @Test
    void givenInvalidIdThenAddResourceReturnFalse() {

        assertEquals(false, marketplace.addResource(5,100));
    }

    @Test
    void givenInvalidAmountThenAddResourceReturnFalse() {

        assertEquals(false, marketplace.addResource(1,-100));
    }

    @Test
    void givenValidThenRemoveResource() {

        assertEquals(true, marketplace.removeResource(1,100));
        assertEquals(900, marketplace.marketResources.get(0).getQuantity());
    }

    @Test
    void givenInvalidIdThenRemoveResourceReturnFalse() {

        assertEquals(false, marketplace.removeResource(5,100));
    }

    @Test
    void givenInvalidAmountThenRemoveResourceReturnFalse() {

        assertEquals(false, marketplace.removeResource(1,-100));
    }

    @Test
    void givenValidThenAddFunds() {

        assertEquals(10100, marketplace.addFunds("User One", 100));
    }

    @Test
    void givenInvalidIdThenAddFundsReturnError() {

        assertEquals(-1, marketplace.addFunds("User Five",100));
    }

    @Test
    void givenInvalidAmountThenAddFundsReturnError() {

        assertEquals(-1, marketplace.addFunds("User One",-100));
    }

    @Test
    void givenValidThenRemoveFunds() {

        assertEquals(9900, marketplace.deductFunds("User One", 100));
    }

    @Test
    void givenInvalidIdThenRemoveFundsReturnError() {

        assertEquals(-1, marketplace.deductFunds("User Five",100));
    }

    @Test
    void givenInvalidAmountThenRemoveFundsReturnError() {

        assertEquals(-1, marketplace.deductFunds("User One",-100));
    }

    @Test
    void givenNotEnoughInAccountAmountThenRemoveFundsReturnError() {

        assertEquals(-1, marketplace.deductFunds("User Four",-100));
    }

    @Test
    void givenValidThenTransferFunds() {

        assertEquals(true, marketplace.transferFunds("User One", "User Two", 500));

        assertEquals(9500, marketplace.getFunds("User One"));
        assertEquals(600, marketplace.getFunds("User Two"));

    }

    @Test
    void givenSourceInvalidIdThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User Five", "User Two", 500));

        assertEquals(100, marketplace.getFunds("User Two"));
    }

    @Test
    void givenDestinationInvalidIdThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User One", "User Five", 500));

        assertEquals(10000, marketplace.getFunds("User One"));
    }

    @Test
    void givenInvalidAmountThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User One", "User Five", -500));

        assertEquals(10000, marketplace.getFunds("User One"));
        assertEquals(100, marketplace.getFunds("User Two"));
    }

    @Test
    void givenNotEnoughInAccountAmountThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User Five", "User One", 500));

        assertEquals(10000, marketplace.getFunds("User One"));
        assertEquals(100, marketplace.getFunds("User Two"));
    }

    @Test
    void givenSourceAndDestinationSameThenTransferFundsReturnFalse() {

        assertEquals(false, marketplace.transferFunds("User One", "User One", 500));

        assertEquals(10000, marketplace.getFunds("User One"));
    }
}