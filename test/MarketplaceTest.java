import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MarketplaceTest {


    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    Resource wood = new Resource(1, 1, 1000, "wood", 1);
    Resource iron = new Resource(2, 2, 1000, "iron", 2);
    Resource silver = new Resource(3, 5, 1000, "silver",5);
    Resource gold = new Resource(4, 10, 1000, "gold",10);
    Marketplace marketplace = new Marketplace(resourceList);

    ArrayList<User> userList = new ArrayList<User>();
    User user1 = new User("User One", "password", resourceList, 10000);
    User user2 = new User("User Two", "password", resourceList, 100);
    User user3 = new User("User Three", "password", resourceList, 10000);
    User user4 = new User("User Four", "password", resourceList, 1);

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

    }

    // Test to check that the marketplace is initialised correctly
    // by asserting that the marketplace contains the correct values for wood
    @Test
    void givenValidThenGetResourceQuantity() {

        assertEquals(wood, marketplace.getResourceDetails(1));
        assertEquals(1, marketplace.getResourceDetails(1).getId());
        assertEquals(1, marketplace.getResourceDetails(1).getCost());
        assertEquals(1000, marketplace.getResourceDetails(1).getQuantity());
        assertEquals("wood", marketplace.getResourceDetails(1).getName());
    }

    // Test to check that the marketplace returns null when given an invalid ID
    @Test
    void givenInvalidThenGetResourceQuantityReturnsNull() {

        assertEquals(null, marketplace.getResourceDetails(5));
    }

    
    // Test to check that the marketplace returns the correct quantity of a resource
    @Test
    void givenValidThenGetResourceDetails() {

        assertEquals(1000, marketplace.getResourceQuantity(1));
    }

    // Test to check that the marketplace returns -1 when given an invalid resourceID
    @Test
    void givenInvalidThenGetResourceDetailsReturnsError() {

        assertEquals(-1, marketplace.getResourceQuantity(6));
    }

    // Test to check that the marketplace returns the correct quantity of a resource after a resource
    // has been added
    @Test
    void givenValidThenAddResourceToMarket() {
        assertEquals(true, marketplace.addResourceToMarket(1,100));
        assertEquals(1100, marketplace.getMarketResources().get(0).getQuantity());
    }

    // Test to check that addResourceToMarket returns false when given an invalid resourceID
    // so no resource is added
    @Test
    void givenInvalidIdThenAddResourceToMarketReturnFalse() {

        assertEquals(false, marketplace.addResourceToMarket(6,100));
    }

    // Test to check that addResourceToMarket returns false when given an invalid quantity
    // In this instance a negative quantity is given
    @Test
    void givenInvalidAmountThenAddResourceToMarketReturnFalse() {

        assertEquals(false, marketplace.addResourceToMarket(1,-100));
    }

    // Test to check that the marketplace returns the correct quantity of a resource after a resource
    // has been removed
    @Test
    void givenValidThenRemoveResourceFromMarket() {

        assertEquals(true, marketplace.removeResourceFromMarket(1,100));
        assertEquals(900, marketplace.marketResources.get(0).getQuantity());
    }

    // Test to check that removeResourceFromMarket returns false when given an invalid resourceID
    // so no resource is removed
    @Test
    void givenInvalidIdThenRemoveResourceFromMarketReturnFalse() {

        assertEquals(false, marketplace.removeResourceFromMarket(6,100));
    }

    // Test to check that removeResourceFromMarket returns false when given an invalid quantity
    // In this instance a negative quantity is given
    @Test
    void givenInvalidAmountThenRemoveResourceFromMarketReturnFalse() {

        assertEquals(false, marketplace.removeResourceFromMarket(1,-100));
    }

    // Test to check the marketplace returns the correct total cost of a resource
    // depending on provided resourceID and quantity
    @Test
    void givenValidThenCalculateTotal() {

        assertEquals(100, marketplace.calculateTotalCost(100, 1));
    }

    // Test to check that calculateTotalCost returns -1 when given an invalid resourceID
    @Test
    void givenInvalidIDThenCalculateTotalReturnsError() {

        assertEquals(-1, marketplace.calculateTotalCost(100, 6));
    }

    // Test to check that calculateTotalCost returns -1 when given an invalid quantity
    @Test
    void givenInvalidQuantityThenCalculateTotalReturnsError() {

        assertEquals(-1, marketplace.calculateTotalCost(-100, 1));
    }
}